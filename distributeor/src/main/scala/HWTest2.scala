import com.asiainfo.Conf
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/8/26.
 *  测试在HW集群上是否能正常的运行且能读取到kafka中的数据
 */
object HWTest2 {
  def main(args: Array[String]) {

    if(args.length<1){
      System.err.println("please input topic")
      System.exit(-1)
    }
    val consumerFrom = Set(args(0))
    println("-"*50)
    val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka,
                                          "group.id" -> "aitest",
                                          "auto.offset.reset"->"smallest")

    val sparkConf = new SparkConf().setAppName("AiQcdEventTest") //.setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5*60))

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, consumerFrom)
    messages.print()

    ssc.start();
    ssc.awaitTermination();
  }
}
