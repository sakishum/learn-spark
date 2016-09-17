import kafka.api._
import kafka.common.TopicAndPartition
import kafka.consumer.SimpleConsumer
import kafka.utils.{ZKStringSerializer, ZKGroupTopicDirs, ZkUtils}
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.exception.ZkNoNodeException

import scala.collection.mutable


/**
 * Created by migle on 2016/9/8.
 */
object SimpleDemo {
  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x")

    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect"-> "vm-centos-01:2181",
      "group.id" -> "g2")


    val demo = new SimpleDemo(kafkaparam)
     val tps =  demo.getPartitions(topics.toSeq)


    demo.getGroupOffset(tps.get).foreach(m=>println("%s-%s-%s".format(m._1.topic,m._1.partition,m._2)))


//
//
//        println("========================")
//        tps.foreach(println)
//        println("========================")
//        demo.getLeaderOffsets(tps.get,OffsetRequest.LatestTime,100).foreach(
//        x=>x.foreach(m=>m._2.foreach{
//          o=>println("[%s-%s==%s-%s-%s]".format(m._1.topic,m._1.partition,o._1,o._2,o._3))
//      }))



//        val simple = new SimpleConsumer("vm-centos-01", 9092, 100000,
//          64 * 1024, "g2")
//
//    val rinfo = simple.fetchOffsets(OffsetFetchRequest("g2",tps.get,versionId=0)).requestInfo
//        rinfo.foreach{m=>println(m._1.topic + ":" +  m._1.partition + " : " + m._2.toString)}





//    println(demo.findLeader("sdi_scdt_x", 0).get)
//    println(demo.findLeader("sdi_scdt_x", 1).get)


//    val tmr = new TopicMetadataRequest(TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, Seq())
//    val tmresp = simple.send(tmr)
//    tmresp.topicsMetadata.foreach(tm => {
//      tm.partitionsMetadata.foreach(pm => {
//        //topic,partition,leader,replicas,isr
//        println("topic:" + tm.topic + pm.toString())
//      })
//    })


//    println("simple consumer client")
//
//    val req = new FetchRequestBuilder()
//      .clientId("gx")
//      .addFetch("sdi_scdt_x", 1, 0L, 200)
//      .build()
//
//    val resp = simple.fetch(req)
//    //println(resp.messageSet("sdi_scdt_x", 1).size)
//    resp.messageSet("sdi_scdt_x", 1).foreach(messageAndOffset => {
//      //println(messageAndOffset.offset)
//      println(messageAndOffset.nextOffset)
//      val payload = messageAndOffset.message.payload;
//      payload.limit()
//      val bytes = new Array[Byte](payload.limit);
//      payload.get(bytes);
//      System.out.println(new String(bytes, "UTF-8"));
//    })
  }
}

class SimpleDemo(val kafkaParams: Map[String, String]) {
//group.id,zookeeper
  val brokers = kafkaParams.get("metadata.broker.list")
    .orElse(kafkaParams.get("bootstrap.servers"))
    .getOrElse(throw new Exception(
      "Must specify metadata.broker.list or bootstrap.servers")).split(",").map(x => {
    val hp = x.split(":")
    (hp(0), hp(1).toInt)
  })

  /**
   * 返回此leader可以
   * @param topicAndPartitions
   * @param before
   * @param maxNumOffsets   最大返回几个offset? 有什么作用？
   * @return
   */
  def getLeaderOffsets(topicAndPartitions: Seq[TopicAndPartition],before: Long,maxNumOffsets: Int): Option[Map[TopicAndPartition, Seq[(String, Int, Long)]]] = {
   val leaders = findLeaders(topicAndPartitions)
   withBrokers(brokers){
     consumer=>{
       val reqMap = topicAndPartitions.map(tp=>(tp,PartitionOffsetRequestInfo(before, maxNumOffsets))).toMap
       val req = OffsetRequest(reqMap)
       val resp = consumer.getOffsetsBefore(req)
       val respMap = resp.partitionErrorAndOffsets
       respMap.foreach(_.toString())
       //出错时处理！！！没有访问过的topic会抛出UnknownTopicOrPartitionException
       val res =  respMap.map(m=>(m._1,m._2.offsets.map(x=>
         {
           println(m._1.toString() + ":" + m._2.offsets)
           (consumer.host,consumer.port,x)}))
       ).toMap

       return Some(res)
     }
   }
    None
  }

  /**
   * 返回此consumer group当前offset
   * @param tps
   * @return
   */
  private def getGroupOffset(tps:Seq[TopicAndPartition]):Map[TopicAndPartition, Long]={
    val zkClient = new ZkClient(kafkaParams.get("zookeeper.connect").get,30000, 30000, ZKStringSerializer)
    val offsetMap: mutable.Map[TopicAndPartition, Long] = mutable.Map()
    tps.foreach(tp=>{
      val topicDirs = new ZKGroupTopicDirs(kafkaParams.get("group.id").getOrElse("groupid"), tp.topic)
      try {
        val offset = ZkUtils.readData(zkClient, topicDirs.consumerOffsetDir + "/%d".format(tp.partition))._1.toLong
        offsetMap.put(tp,offset)
      } catch {
        case z: ZkNoNodeException =>
          if(ZkUtils.pathExists(zkClient,topicDirs.consumerOffsetDir))
            offsetMap.put(tp,-1L)
          else
            throw z
      }
    })
    zkClient.close()

    offsetMap.toMap
  }




  //  private def getFromOffsets(kafkaParams: Map[String, String],
  //                                     topics: Set[String]
  //                                     ): Map[TopicAndPartition, Long]{
  //
  //  }

  def getPartitions(topics: Seq[String]): Option[Seq[TopicAndPartition]] = {
    getPartitionMetadata(topics).map(tms => tms.flatMap(tm => tm.partitionsMetadata.map(pm => TopicAndPartition(tm.topic, pm.partitionId))))
  }

  def getPartitionMetadata(topics: Seq[String]): Option[Seq[TopicMetadata]] = {
    withBrokers(brokers) {
      consumer => {
        val req = TopicMetadataRequest(
          TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, topics.toSeq)
        val resp = consumer.send(req)
        return Some(resp.topicsMetadata)
      }
    }
    None
  }

  //def findLeader(topic: String, partition: Int):Either[Unit,(String,Int)] = {
  def findLeader(topic: String, partition: Int): Option[(String, Int)] = {
    withBrokers(brokers) {
      consumer => {
        val tmr = new TopicMetadataRequest(TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, Seq(topic))
        val resp = consumer.send(tmr)
        resp.topicsMetadata.find(_.topic == topic).flatMap(tm => {
          tm.partitionsMetadata.find(_.partitionId == partition)
        }).foreach(pm => {
          pm.leader.foreach(hp => {
            return Some(hp.host, hp.port)
          })
        })
      }
    }
    None
  }

  //host: String, port: Int, offset: Long
  def findLeaders(topicAndPartitions: Seq[TopicAndPartition]): Option[Map[TopicAndPartition, (String, Int)]] = {
    val topics = topicAndPartitions.map(_.topic)
    val gpm = getPartitionMetadata(topics)
    gpm.map { tms => tms.flatMap(tm => tm.partitionsMetadata.map(pm => (TopicAndPartition(tm.topic, pm.partitionId), (pm.leader.get.host, pm.leader.get.port)))).toMap }
  }


  private def withBrokers(brokers: Iterable[(String, Int)])(fn: SimpleConsumer => Any): Unit = {
    brokers.foreach(broker => {
      var consumer: SimpleConsumer = null;
      try {
        consumer = new SimpleConsumer(broker._1, broker._2, 100000,
          64 * 1024, kafkaParams.get("group.id").getOrElse("default.group"))
        fn(consumer)
        // catch exception
      } finally {
        consumer.close
      }
    })
  }
}

