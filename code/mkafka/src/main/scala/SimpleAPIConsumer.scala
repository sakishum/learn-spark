import kafka.api._
import kafka.common.{ErrorMapping, TopicAndPartition}
import kafka.consumer.SimpleConsumer
import kafka.utils.{Logging, ZKGroupTopicDirs, ZKStringSerializer, ZkUtils}
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.exception.ZkNoNodeException

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.NonFatal


/**
 * Created by migle on 2016/9/8.
 */
object SimpleAPIConsumer {
  type Errors = ArrayBuffer[Throwable]

  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x")

    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "group.id" -> "g2")


    val demo = new SimpleAPIConsumer(kafkaparam)
    val tps = demo.getPartitions(topics.toSet)
    println(tps.right)
    //demo.getGroupOffset(tps.right).foreach(m=>println("%s-%s-%s".format(m._1.topic,m._1.partition,m._2)))

  }
}

class SimpleAPIConsumer(val kafkaParams: Map[String, String]) extends Logging {
  import SimpleAPIConsumer.Errors
  //group.id,zookeeper
  val brokers = kafkaParams.get("metadata.broker.list")
    .orElse(kafkaParams.get("bootstrap.servers"))
    .getOrElse(throw new Exception(
      "Must specify metadata.broker.list or bootstrap.servers")).split(",").map(x => {
    val hp = x.split(":")
    (hp(0), hp(1).toInt)
  })



  /**
   * 返回此leader
   * @param topicAndPartitions
   * @param before
   * @param maxNumOffsets   最大返回几个offset? 有什么作用？
   * @return
   */
  def getLeaderOffsets(topicAndPartitions: Set[TopicAndPartition], before: Long, maxNumOffsets: Int): Option[Map[TopicAndPartition, Seq[(String, Int, Long)]]] = {
    val leaders = findLeaders(topicAndPartitions)
    val err = new Errors
    withBrokers(brokers,err) {
      consumer => {
        val reqMap = topicAndPartitions.map(tp => (tp, PartitionOffsetRequestInfo(before, maxNumOffsets))).toMap
        val req = OffsetRequest(reqMap)
        val resp = consumer.getOffsetsBefore(req)
        val respMap = resp.partitionErrorAndOffsets
        respMap.foreach(_.toString())
        //出错时处理！！！没有访问过的topic会抛出UnknownTopicOrPartitionException
        val res = respMap.map(m => (m._1, m._2.offsets.map(x => {
          println(m._1.toString() + ":" + m._2.offsets)
          (consumer.host, consumer.port, x)
        }))
        ).toMap

        return Some(res)
      }
    }
    None
  }

  //  def fetchMessage(tpo: (TopicAndPartition, Long)): Iterator[MessageAndOffset] = {
  //    withBrokers(findLeader(tpo._1.topic, tpo._1.partition)) {
  //      consumer => {
  //        /** the number of byes of messages to attempt to fetch  1MB */
  //        val req = new FetchRequestBuilder().addFetch(tpo._1.topic, tpo._1.partition, tpo._2, 1024 * 1024).build()
  //        val resp = consumer.fetch(req)
  //        return resp.messageSet(tpo._1.topic, tpo._1.partition).iterator.dropWhile(_.offset < tpo._2)
  //      }
  //    }
  //    null
  //  }

  /**
   * 返回此consumer group当前offset
   * @param tps
   * @return
   */
  private def getGroupOffset(tps: Seq[TopicAndPartition]): Map[TopicAndPartition, Long] = {
    val zkClient = new ZkClient(kafkaParams.get("zookeeper.connect").get, 30000, 30000, ZKStringSerializer)
    val offsetMap: mutable.Map[TopicAndPartition, Long] = mutable.Map()
    tps.foreach(tp => {
      val topicDirs = new ZKGroupTopicDirs(kafkaParams.get("group.id").getOrElse("groupid"), tp.topic)
      try {
        val offset = ZkUtils.readData(zkClient, topicDirs.consumerOffsetDir + "/%d".format(tp.partition))._1.toLong
        offsetMap.put(tp, offset)
      } catch {
        case z: ZkNoNodeException =>
          if (ZkUtils.pathExists(zkClient, topicDirs.consumerOffsetDir))
            offsetMap.put(tp, -1L)
          else
            offsetMap.put(tp, 0L) //如果没有，则没有则从0开始
        //throw z
      }
    })
    zkClient.close()
    offsetMap.toMap
  }


//  def getLogSize():Long={
//
//  }


  /**
   * 查询topic和patition的对应
   * @param topics
   * @return
   */
  def getPartitions(topics: Set[String]): Either[Errors, Set[TopicAndPartition]] = {
    getTopicMetadata(topics).right.map { tms =>
      tms.flatMap { tm =>
        tm.partitionsMetadata.map { pm =>
          TopicAndPartition(tm.topic, pm.partitionId)
        }
      }
    }
  }

  /**
   * 查询topic的元数据
   * https://cwiki.apache.org/confluence/display/KAFKA/A+Guide+To+The+Kafka+Protocol#AGuideToTheKafkaProtocol-MetadataAPI
   * @param topics
   * @return
   */
  def getTopicMetadata(topics: Set[String]): Either[Errors, Set[TopicMetadata]] = {
    val errors = new Errors
    withBrokers(Random.shuffle(brokers), errors) {
      consumer => {
        val req = TopicMetadataRequest(
          TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, topics.toSeq)
        val resp = consumer.send(req)
        //kafka.common.ErrorMapping中有错误编码的映射
        val respErrs = resp.topicsMetadata.filter(tm => tm.errorCode != ErrorMapping.NoError)
        if (respErrs.isEmpty) {
          return Right(resp.topicsMetadata.toSet)
        } else {
          respErrs.foreach { e => {
            val cause = ErrorMapping.exceptionFor(e.errorCode)
            val msg = s"Error getting partition metadata for '${e.topic}'. Does the topic exist?"
            errors.append(new Exception())
          }
          }
        }
      }
    }
    Left(errors)
  }

  /**
   * 返回指定(topic,partition)leader的(host,port)
   * @param topic
   * @param partition
   * @return
   */
  def findLeader(topic: String, partition: Int): Either[Errors, (String, Int)] = {

    val tms = getTopicMetadata(Set(topic))

    tms.right.get.find(_.topic == topic).flatMap(t =>
      t.partitionsMetadata.find(_.partitionId == partition)
    ).foreach(pm => {
      pm.leader.foreach(hp => {
        return Right(hp.host, hp.port)
      })
    })

    Left(tms.left.get)
  }

  /**
   * 返回指定Set(topic,partition)的leader
   * @param topicAndPartitions
   * @return
   */
  def findLeaders(topicAndPartitions: Set[TopicAndPartition]): Either[Errors, Map[TopicAndPartition, (String, Int)]] = {
    val topics = topicAndPartitions.map(_.topic)
    val resp = getTopicMetadata(topics).right
    val leaderMap = resp.flatMap { tms =>
      val m = tms.flatMap { tm =>
        tm.partitionsMetadata.flatMap { pm =>
          val tp = TopicAndPartition(tm.topic, pm.partitionId)
          if (topicAndPartitions(tp)) {
            pm.leader.map(leader => {
              tp ->(leader.host, leader.port)
            })
          } else {
            None
          }
        }
      }.toMap
      if (m.keys.size == topicAndPartitions.size) {
        Right(m)
      } else {
        val missing = topicAndPartitions.diff(m.keySet)
        val err = new Errors
        err.append(new Exception(s"Couldn't find leaders for ${missing}"))
        Left(err)
      }
    }
    leaderMap
  }


  private def withBrokers(brokers: Iterable[(String, Int)], errs: Errors)(fn: SimpleConsumer => Any): Unit = {
    brokers.foreach(broker => {
      var consumer: SimpleConsumer = null;
      try {
        consumer = new SimpleConsumer(broker._1, broker._2, 100000,
          64 * 1024, kafkaParams.get("group.id").getOrElse("default.group"))
        fn(consumer)
      } catch {
        case NonFatal(e) =>
          errs.append(e)
      } finally {
        consumer.close
      }
    })
  }


//  private def withZookeeper(errs: Errors)(fn:ZkClient=>Any):Unit={
//    val zkInfo = kafkaParams.get("zookeeper.connect").getOrElse(throw new Exception(
//      "Must specify zookeeper.connect for read data from zookeeper"))
//    val zkClient = new ZkClient(zkInfo, 30000, 30000, ZKStringSerializer)
//  }
}

