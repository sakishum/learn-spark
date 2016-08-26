import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import com.asiainfo.Conf;
/**
 * Created by migle on 2016/8/10.
 */
object KafkaStream1 {
def main(args:Array[String]): Unit ={
  val sparkConf = new SparkConf().setAppName("KafkaStreamDist").setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
  val ssc = new StreamingContext(sparkConf, Seconds(5))
  val topicsSet=Set(Conf.consume_topic_netpay,Conf.consume_topic_order,Conf.consume_topic_usim)
  val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka,"group.id" -> Conf.groupid)

  //DirectStream
  val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicsSet)
  messages.map(x=>x._2).print()

  //messages.print();

  ssc.start()
  ssc.awaitTermination()
}

}
