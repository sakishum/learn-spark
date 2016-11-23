import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/9/23.
 */
object KafkaStreaming {
  def main(args: Array[String]) {
    val topics = Seq("sdi_scdt_x","sdi_scdt_3","kafkatest")
    val conf = new SparkConf().setAppName("StreamingWithBroadcast").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(10))

    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "auto.offset.reset" -> "smallest",
      "group.id" -> "g4")

    val k1 = topics.map(t=>KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaparam,Set(t)))

    val kafka = ssc.union(k1)

     // .map(_._2)
    kafka.repartition(20)

    kafka.foreachRDD(rdd=>{
      //rdd.repartition(15)
      //println(rdd.partitions.size)
      rdd.foreach(println)
    })

    ssc.start()
    ssc.awaitTermination()

  }
}
