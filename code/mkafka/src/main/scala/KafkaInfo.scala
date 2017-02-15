import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

import com.aistream.kafka.SecurityUtils
import kafka.common.TopicAndPartition

/**
  * 统计各topic的partition中数据分布情况
  */
object KafkaInfo {
  private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")

  def main(args: Array[String]): Unit = {

    SecurityUtils.securityPrepare()
    val kafkaparam = Map("bootstrap.servers" -> args(0))
    val topics = args(1).split(",").toSet //Set("topic_lte_http")

    val kafka = new SimpleAPIConsumer(kafkaparam)
    var tpo = kafka.getLatestLeaderOffsets(kafka.getPartitions(topics).right.get).right.get
    while (true) {
      //tpo.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset)))
      //println("sleep 1m")
      TimeUnit.MINUTES.sleep(1)
      val time = LocalDateTime.now.toString
      var tpo2 = kafka.getLatestLeaderOffsets(kafka.getPartitions(topics).right.get).right.get
      //tpo2.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset)))
      //println("size----")
      //todo:partition有变化时？
      var r = tpo.map(k => (k._1, tpo2.get(k._1).get.offset - k._2.offset))
      println("--"*20)
      r.foreach(kv => println("size:%s:%s-%s:%d".format(time, kv._1.topic, kv._1.partition, kv._2)))
      printSummary(r)
      println("--"*20)
      //summary
      //按topic求和,均值,最大值,最小值,方差
      tpo = tpo2

    }


    //  .foreach(m=>m.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset))))
    //println("--"*30)
    //kafka.getEarliestLeaderOffsets(tp.get).right.foreach(m=>m.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset))))
  }

  def printSummary(tpl: Map[TopicAndPartition, Long]): Unit = {
    val tmp = tpl.groupBy(k => k._1.topic)
    val tms = tmp.map(k => k._1 -> summary(k._2.map(t => t._2)))
    tms.foreach(t => println(("time:%s|topic:%-18s" +
      "|partition:%-3.0f" +
      "|sum:%-12.0f" +
      "|max:%-10.0f" +
      "|min:%-10.0f" +
      "|avg:%-10.0f|" +
      "sd:%.2f").format(
      LocalDateTime.now.format(dtf),
      t._1,
      t._2("count"),
      t._2("sum"),
      t._2("max"),
      t._2("min"),
      t._2("avg"),
      t._2("sd")
    )))
  }

  def summary(list: Iterable[Long])= {
    val count = list.size
    val sum =   list.sum //list.fold(0L)((a, b) => a + b)
    val avg =   sum/count

    val m = Map[String,Double](
      "count" -> count,
      "sum"   -> sum,
      "max"   -> list.max,
      "min"   -> list.min,
      "avg"   -> avg,
      "sd"    -> sd(list)
    )
    //标准差
    m
  }

  def sd(implicit list:Iterable[Long]):Double={
    val avg = list.sum/list.size
    val s2  = list.map(x=>math.pow(x-avg,2)).sum/list.size
    math.sqrt(s2)
  }
}