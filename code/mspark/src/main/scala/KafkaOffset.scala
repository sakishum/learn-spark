import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils}

/**
  * Created by migle on 2016/10/9.
  */
object KafkaOffset {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamingWithKafka").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(10))

    val topics = Set("sdi_scdt_x","sdi_scdt_3")
    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "auto.offset.reset" -> "smallest",
      "group.id" -> "g5")

    val kafka = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaparam,topics).transform(rdd=>{
      val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      for(offset <- offsets){
        println(offset.toString())
      }

      rdd
    }).map(_._2)
    // kafka.foreachRDD(_.foreachPartition(_.foreach(println)))

    kafka.foreachRDD(rdd=>{
      println(kafka)
      rdd.foreach(println)
    })
    kafka.print()
    println("=="*10)
    ssc.start()
    ssc.awaitTermination()
  }
}
