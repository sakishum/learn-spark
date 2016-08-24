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
   *
   * @param topics
   * @param time    OffsetRequest.LatestTime  OffsetRequest.EarliestTime
   * @return
   */
  def getOffsets(topics: Set[String],time:Long): Map[TopicAndPartition, Long] = {
    //TODO 看看org.apache.spark.streaming.kafka.KafkaCluster.scala的实现，理解kafka的原理
    val consumer = new SimpleConsumer("vm-centos-00", 9092, 100000, 64 * 1024, Conf.groupid)
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

 //TODO 从保存位置取出

  def main(args: Array[String]) {
    //getOffsets(topics,-1).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
    val topics = Set("test-1","test-2","test3","topic-2")
    getLatestOffset(topics).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
    println("-----"*10)
    getEarliestOffset(topics).foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
  }
}
