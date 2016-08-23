
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/8/10.
 */
object KafkaStreamDist {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("KafkaStreamDist").setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val topicsSet = Set("test-1")

    //largest
    //,"auto.offset.reset"->"smallest"

    val kafkaParams = Map[String, String]("metadata.broker.list" -> "vm-centos-00:9092","group.id"->"test2")
    //默认是largst   "auto.offset.reset"->"smallest"
    // val kc = new KafkaCluster(kafkaParams)

    val topicPartition = TopicAndPartition("test-1", 0)

    val dstream =   KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String,String)](
            ssc, kafkaParams, Map(topicPartition -> 11L),
            (m: MessageAndMetadata[String, String]) => (m.topic,m.message()))
      dstream.map(k=>(k._1,k._2)).foreachRDD(rdd=>{
        rdd.foreach(line=>{
          println("topic:" + line._1 + " message:" + line._2)
        })
      })


    ///显示topic的元数据信息
//    val dstream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
//      ssc, kafkaParams, topicsSet)
//    var offsetRanges = Array[OffsetRange]()
//    dstream.transform(rdd => {
//      offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//      rdd
//    }).foreachRDD(rdd => {
//      for (o <- offsetRanges) {
//        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
//      }
//    })
    //dstream.print()

    //val topicMap = topicsSet.map((_, 2.toInt)).toMap
    //  val dstream =   KafkaUtils.createStream(ssc,"vm-centos-00:2181","group-test",topicMap);
    //  dstream.print()


    //messages.map(x=>x._2).map(x=>x.replaceAll(" ",":")).print()
    //messages.print();
    //messages.print();

    //  var offsetRanges = Array[OffsetRange]()
    //  messages.transform({ rdd =>
    //   offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    //   rdd
    // }).foreachRDD(rdd=>for (o <- offsetRanges) {
    //    println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
    //  })

    ssc.start()
    ssc.awaitTermination()
  }

}
