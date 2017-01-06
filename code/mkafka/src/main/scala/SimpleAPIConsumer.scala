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
  case class LeaderOffset(host: String, port: Int, offset: Long)

  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x")
    val test = Seq("x")
    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "group.id" -> "g2")


    val demo = new SimpleAPIConsumer(kafkaparam)
    val tps = demo.getPartitions(topics.toSet)
    tps.right.get.foreach(tp=>println("%s:%s".format(tp.topic,tp.partition)))
    demo.getLatestLeaderOffsets(demo.getPartitions(topics.toSet).right.get).right.foreach(println)

    demo.getLatestLeaderOffsets(demo.getPartitions(topics.toSet).right.get).right.foreach(m=>m.foreach(kv=>println("%s:%s".format(kv._1,kv._2))))
    //demo.getGroupOffset(tps.right).foreach(m=>println("%s-%s-%s".format(m._1.topic,m._1.partition,m._2)))

  }
}

class SimpleAPIConsumer(val kafkaParams: Map[String, String]) extends Logging {
  import SimpleAPIConsumer.Errors
  import SimpleAPIConsumer.LeaderOffset

  //group.id,zookeeper
  val brokers = kafkaParams.get("metadata.broker.list")
    .orElse(kafkaParams.get("bootstrap.servers"))
    .getOrElse(throw new Exception(
      "Must specify metadata.broker.list or bootstrap.servers")).split(",").map(x => {
    val hp = x.split(":")
    (hp(0), hp(1).toInt)
  })

  def getLatestLeaderOffsets(
                              topicAndPartitions: Set[TopicAndPartition]
                            ): Either[Errors, Map[TopicAndPartition, LeaderOffset]] =
    getLeaderOffsets(topicAndPartitions, OffsetRequest.LatestTime)

  def getEarliestLeaderOffsets(
                                topicAndPartitions: Set[TopicAndPartition]
                              ): Either[Errors, Map[TopicAndPartition, LeaderOffset]] =
    getLeaderOffsets(topicAndPartitions, OffsetRequest.EarliestTime)

  def getLeaderOffsets(
                        topicAndPartitions: Set[TopicAndPartition],
                        before: Long
                      ): Either[Errors, Map[TopicAndPartition, LeaderOffset]] = {
    getLeaderOffsets(topicAndPartitions, before, 1).right.map { r =>
      r.map { kv =>
        kv._1 -> kv._2.head
      }
    }
  }


  /**
   * @param topicAndPartitions
   * @param before
   * @param maxNumOffsets   最大返回几个offset? 有什么作用？
   * @return
   */
  def getLeaderOffsets(
                        topicAndPartitions: Set[TopicAndPartition],
                        before: Long,
                        maxNumOffsets: Int
                      ): Either[Errors, Map[TopicAndPartition, Seq[LeaderOffset]]] = {
    findLeaders(topicAndPartitions).right.flatMap { tpToLeader =>
      val leaderToTp: Map[(String, Int), Seq[TopicAndPartition]] = flip(tpToLeader)
      val leaders = leaderToTp.keys
      var result = Map[TopicAndPartition, Seq[LeaderOffset]]()
      val errs = new Errors
      withBrokers(leaders, errs) { consumer =>
        val partitionsToGetOffsets: Seq[TopicAndPartition] =
          leaderToTp((consumer.host, consumer.port))
        val reqMap = partitionsToGetOffsets.map { tp: TopicAndPartition =>
          tp -> PartitionOffsetRequestInfo(before, maxNumOffsets)
        }.toMap
        val req = OffsetRequest(reqMap)
        val resp = consumer.getOffsetsBefore(req)
        val respMap = resp.partitionErrorAndOffsets
        partitionsToGetOffsets.foreach { tp: TopicAndPartition =>
          respMap.get(tp).foreach { por: PartitionOffsetsResponse =>
            if (por.error == ErrorMapping.NoError) {
              if (por.offsets.nonEmpty) {
                result += tp -> por.offsets.map { off =>
                  LeaderOffset(consumer.host, consumer.port, off)
                }
              } else {
                errs.append(new Exception(
                  s"Empty offsets for ${tp}, is ${before} before log beginning?"))
              }
            } else {
              errs.append(ErrorMapping.exceptionFor(por.error))
            }
          }
        }
        if (result.keys.size == topicAndPartitions.size) {
          return Right(result)
        }
      }
      val missing = topicAndPartitions.diff(result.keySet)
      errs.append(new Exception(s"Couldn't find leader offsets for ${missing}"))
      Left(errs)
    }
  }


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

  private def flip[K, V](m: Map[K, V]): Map[V, Seq[K]] =
    m.groupBy(_._2).map { kv =>
      kv._1 -> kv._2.keys.toSeq
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
}

