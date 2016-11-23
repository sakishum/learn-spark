import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/9/22.
 * 带checkpoint的kafka消费
 * KafkaUtils.createDirectStream不会将offset提交到zk中，做了checkpoint之后，driver重启后从检查点恢复，会接着之前的offset继续消费
 *
 */
object KafkaStreamingWithChk {
  def kafkaContext(chkdir:String)={
    val conf = new SparkConf().setAppName("StreamingWithKafka").setMaster("local[2]")
    val ssc = new StreamingContext(conf,Seconds(60))

    val topics = Set("sdi_scdt_x")
    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "auto.offset.reset" -> "smallest",
      "group.id" -> "g4")

    val kafka = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaparam,topics).map(_._2)
   // kafka.foreachRDD(_.foreachPartition(_.foreach(println)))

    kafka.foreachRDD(rdd=>{
      println(rdd.partitions.size)
//       rdd.distinct()
//      rdd.foreachPartition(p=>{
//        //
//      })
    })
    //kafka.checkpoint()  //这个可以设置检查点的时间间隔,太频繁的保存检查点会影响性能
    ssc.checkpoint(chkdir)
    ssc
  }

  def main(args: Array[String]) {
    val chkdir ="hdfs://vm-centos-01:9999/user/migle/chkpoint2"
    val chkssc = StreamingContext.getOrCreate(chkdir,()=>kafkaContext(chkdir))
    chkssc.start()
    chkssc.awaitTermination()
  }
}
