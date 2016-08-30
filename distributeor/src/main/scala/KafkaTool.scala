package org.apache.spark.streaming.kafka
import kafka.common.TopicAndPartition

/**
 * Created by migle on 2016/8/25.
 *
 */
object KafkaTool {

  def getOffsets(kafkaParams: Map[String, String],topics: Set[String]): Map[TopicAndPartition, Long] = {
    val kc = new KafkaCluster(kafkaParams)
    //TODO:自己实现一个
    KafkaUtils.getFromOffsets(kc,kafkaParams,topics)
  }
}
