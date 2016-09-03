import com.asiainfo.Conf
import com.asiainfo.common.KafkaTopicOffsetTool
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import kafka.serializer.StringDecoder
import collection.JavaConverters._
/**
 * Created by migle on 2016/8/26.
 *  测试在HW集群上是否能正常的运行且能读取到kafka中的数据
 */
object HWTest2 {
  def main(args: Array[String]) {

    val topics = Set(Conf.consume_topic_netpay,Conf.consume_topic_order,Conf.consume_topic_usim)
    println("-"*50)
    val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka,
                                          "group.id" -> "aitest")

    val sparkConf = new SparkConf().setAppName("AiQcdEventTest") //.setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5*60))
    val offsets = KafkaTopicOffsetTool.getLargstOffsets(topics.asJava).asInstanceOf[Map[TopicAndPartition, Long]]


    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String,String,String)](
            ssc, kafkaParams, offsets,
            (m: MessageAndMetadata[String, String]) => (m.topic,m.key(),m.message()))

    messages.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
