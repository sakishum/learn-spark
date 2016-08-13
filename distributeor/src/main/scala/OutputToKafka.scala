import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/8/12.
 */
object OutputToKafka {
  def main(args: Array[String]) {

    val consumerFrom = Set("topic-1")
    val proudceTo = "topic-p-1"

    val param = Array(Conf.kafka, "10", "20");
    val Array(brokers, messagesPerSec, wordsPerMessage) = param

    val sparkConf = new SparkConf().setAppName("KafkaStreamDist").setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka,"group.id"->"test")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, consumerFrom)

    //TODO 应该根据topic为选择格式
    messages.map(x => x._2).map(x => {
     val a = x.split("\\|")
      (a(0),a(1),a(2))
    }).print()


    //      .foreachRDD(rdd=>{
    //      rdd.foreachPartition(p=>{
    //
    //        p.foreach(line=>{
    //          //初始化redis连接
    //          val jedis = new Jedis("192.168.99.130");
    //          jedis.auth("redispass");
    //          //拉取生效规则
    //
    //
    //          //判断所有规则
    //
    //
    //
    //          val props = new HashMap[String, Object]()
    //          props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    //          props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    //            "org.apache.kafka.common.serialization.StringSerializer")
    //          props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    //            "org.apache.kafka.common.serialization.StringSerializer")
    //          val producer = new KafkaProducer[String, String](props)
    //
    //          //根据不同的规则放入不同的topic，有可能是多个topic
    //          val pr =  new ProducerRecord[String, String](proudceTo,null,"xxxxx-"+line);
    //          producer.send(pr);
    //        })
    //      })
    //    })

    ssc.start();
    ssc.awaitTermination();
  }
}
