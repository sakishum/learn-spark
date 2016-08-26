import com.asiainfo.Conf
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.{KafkaUtils, KafkaTool}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/8/26.
 */
object HWTest {
  def main(args: Array[String]) {
    val topics = Set(Conf.consume_topic_netpay,Conf.consume_topic_order,Conf.consume_topic_usim)
    val brokers  = Conf.kafka
    val sparkConf = new SparkConf().setAppName("HWTest")//.setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(20))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "group.id" -> Conf.groupid,"auto.offset.reset"->"smallest")
    //val offsets = KafkaOffsetTool.getLatestOffset(topics)
    //TODO
    val offsets = KafkaTool.getOffsets(kafkaParams,topics)
    // println("-----"*10)
    // offsets.foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
    // println("-----"*10)

    val DStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String,String,String)](
            ssc, kafkaParams, offsets,
            (m: MessageAndMetadata[String, String]) => (m.topic,m.key(),m.message()))

    DStream.print()
    ssc.start()
    ssc.awaitTermination()

  }
}
