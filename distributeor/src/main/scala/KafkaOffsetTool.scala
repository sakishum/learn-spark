package com.asiainfo.spark
import com.asiainfo.Conf
import kafka.api.{OffsetRequest, TopicMetadataRequest, TopicMetadataResponse}
import kafka.common.TopicAndPartition
import kafka.consumer.SimpleConsumer

/**
 * Created by migle on 2016/8/24.
 *  获取(topic,partition)的offset的工具类
 *  原理还有些不清楚的地方，看api先实现了一个不太完善的功能
 *
 */
object KafkaOffsetTool {
  /**
   * @param topics
   * @param time    OffsetRequest.LatestTime  OffsetRequest.EarliestTime
   * @return
   */
  def getOffsets(topics: Set[String],time:Long): Map[TopicAndPartition, Long] = {
    //TODO 看看org.apache.spark.streaming.kafka.KafkaCluster.scala的实现，理解kafka的原理
    val hostport=Conf.kafka.split(",")(0)
    val host=hostport.split(":")(0)
    val port=hostport.split(":")(1)
    println("==="*10)
    println(host)
    println(port.toInt)
    println("==="*10)
    val consumer = new SimpleConsumer(host, port.toInt, 100000, 64 * 1024, Conf.groupid)
    val req = TopicMetadataRequest(TopicMetadataRequest.CurrentVersion, 0, Conf.groupid, topics.toSeq)
    val resp: TopicMetadataResponse = consumer.send(req)
    //val respErrs = resp.topicsMetadata.filter(m => m.errorCode != ErrorMapping.NoError)
    //resp.topicsMetadata.toSet.foreach(println)
    val map = resp.topicsMetadata.map(t => {
      t.partitionsMetadata.map(
        p => {
          val offset:Long = consumer.earliestOrLatestOffset(new TopicAndPartition(t.topic, p.partitionId), time, 0)
          //t.topic +" " + p.partitionId + " " + offset
          Map(TopicAndPartition(t.topic,p.partitionId)->offset)
        }
      )
    }).reduce((a,b)=> a ++ b).reduce(_ ++ _)
    consumer.close()
    map
  }

  def getEarliestOffset(topics:Set[String])=getOffsets(topics, OffsetRequest.EarliestTime)

  def getLatestOffset(topics:Set[String])=getOffsets(topics, OffsetRequest.LatestTime)

  def getTestOffset(topics:Set[String]): Map[TopicAndPartition, Long]={
    val m = Map(
      new TopicAndPartition("sdi_scdt_4",0)->0L,
      new TopicAndPartition("sdi_scdt_4",1)->0L,
      new TopicAndPartition("sdi_scdt_4",2)->0L,
      new TopicAndPartition("sdi_scdt_4",3)->0L,
      new TopicAndPartition("sdi_scdt_4",4)->0L,
      new TopicAndPartition("sdi_scdt_4",5)->0L,
      new TopicAndPartition("sdi_scdt_4",6)->0L,
      new TopicAndPartition("sdi_scdt_4",7)->0L,
      new TopicAndPartition("sdi_scdt_4",8)->0L,
      new TopicAndPartition("sdi_scdt_4",9)->0L,
      new TopicAndPartition("sdi_scdt_3",0)->0L,
      new TopicAndPartition("sdi_scdt_3",1)->0L,
      new TopicAndPartition("sdi_scdt_3",2)->0L,
      new TopicAndPartition("sdi_scdt_3",3)->0L,
      new TopicAndPartition("sdi_scdt_3",4)->0L,
      new TopicAndPartition("sdi_scdt_3",5)->0L,
      new TopicAndPartition("sdi_scdt_3",6)->0L,
      new TopicAndPartition("sdi_scdt_3",7)->0L,
      new TopicAndPartition("sdi_scdt_3",8)->0L,
      new TopicAndPartition("sdi_scdt_3",9)->0L,
      new TopicAndPartition("sdi_scdt_5",0)->0L,
      new TopicAndPartition("sdi_scdt_5",1)->0L,
      new TopicAndPartition("sdi_scdt_5",2)->0L,
      new TopicAndPartition("sdi_scdt_5",3)->0L,
      new TopicAndPartition("sdi_scdt_5",4)->0L,
      new TopicAndPartition("sdi_scdt_5",5)->0L,
      new TopicAndPartition("sdi_scdt_5",6)->0L,
      new TopicAndPartition("sdi_scdt_5",7)->0L,
      new TopicAndPartition("sdi_scdt_5",8)->0L,
      new TopicAndPartition("sdi_scdt_5",9)->0L
    )
     m
  }



 //TODO 从保存位置取出

  def main(args: Array[String]) {
    //getOffsets(topics,-1).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
    val topics = Set(Conf.consume_topic_netpay,Conf.consume_topic_order,Conf.consume_topic_usim)
    getLatestOffset(topics).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
    println("-----"*10)
    getEarliestOffset(topics).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
  }
}
