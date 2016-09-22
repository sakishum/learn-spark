import kafka.serializer.StringDecoder
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/9/21.
 */
object StreamingWithBroadcast {
  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x")
    val conf = new SparkConf().setAppName("StreamingWithBroadcast").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(10))

    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
      "zookeeper.connect" -> "vm-centos-01:2181",
      "auto.offset.reset" -> "smallest",
      "group.id" -> "g4")
    val kafka = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc,kafkaparam,topics).map(_._2)
    kafka.foreachRDD(rdd => {
      val br = TermInfo.getTermInfo(rdd.context)
      //从广播变量中去关联信息
      rdd.foreachPartition(p =>
        p.foreach(
          line => {
            if (br.value.contains(line)) {
              println(br.value.get(line).get)
            } else {
              println("no exists:" + line)
            }
          }
        )
      )
    }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}

object TermInfo {
  @volatile private var terminfo: Broadcast[Map[String, (String, String)]] = null
  //针对数量不大，但有更新的外部关联数据是不是可以这样做？比如终端类型表，敏感词过滤等等数据
  //必要时可以根据累加器或时间来判断是不是要广播来控制频次
  //刷新
  def getTermInfo(sc: SparkContext): Broadcast[Map[String, (String, String)]] = {
    if (terminfo == null) {
      synchronized {
        if (terminfo == null) {
          val rdd = sc.textFile("e:/a.txt").map(line => {
            val s = line.split("\\|")
            (s(0), (s(1), s(2)))
          }).collect().toMap
          terminfo = sc.broadcast(rdd)
        }
      }
    }
    terminfo
  }
}
