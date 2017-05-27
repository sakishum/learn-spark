import kafka.serializer.StringDecoder
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory
import org.apache.spark.sql.{SQLContext, SaveMode}

import scala.collection.JavaConverters._
/**
  * Created by migle on 2017/3/21.
  * Spark Streaming与spark sql结合测试
  */
case class KafkaGnMsg(msisdn:String,busi_id:String)
object QCDGnDataSQL {
  private val log = LoggerFactory.getLogger("QCDGnData")

//  def main(args: Array[String]): Unit = {
//    val topics = Set("topic_gn_general", "topic_gn_http", "topic_lte_general", "topic_lte_http").toSeq
//    val psize = if (args.length >= 1) args(0).toInt else 20
//    val batchDuration = if (args.length >= 2) args(1).toInt else 60
//
//    val conf = new SparkConf().setAppName("QCDGnData")
//    val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka, "group.id" -> Conf.groupid)
//
//    val ssc = new StreamingContext(conf, Seconds(batchDuration))
//
//    val dstreams = topics.map(topic => {
//      KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(topic))
//        .map(x => (topic, x._2))
//    })
//
//    val messages = ssc.union(dstreams).repartition(psize).map(GnStreamUtil.format(_))
////      .filter(_.getOrElse("msisdn","0")!="0")  //过滤空行或手机号为空的数据
////      .map(line=>(line("msisdn")+"|"+ line("busi_id"),1))
////      .reduceByKey(_+_)  //去重
////      .map(_._1)
////      .map(line=>{
////        val tmp= line.split("\\|")
////        Map("phone_no"->tmp(0),"busi_id"->tmp(1))
////      })
//
//    messages.foreachRDD(rdd => {
//      // Get the singleton instance of SQLContext
//      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
//      import sqlContext.implicits._
//      val df = rdd.map(line => KafkaGnMsg(line.getOrElse("msisdn",""),line.getOrElse("busi_id",""))).toDF()
//      df.registerTempTable("kafka_gn_msg")
//      val result = sqlContext.sql("select distinct  msisdn,busi_id from kafka_gn_msg where busi_id  in('1-9','5-61','8-161','8-162')" )
////写入HDFS
////      result.write
////        .mode(SaveMode.Append)  //追加模式，会添加到原有目录下面
////        .save("hdfs://hacluster/yx_qcd/gnstream/gndata-qcd/")
//
////写入Kafka
//  result.foreachPartition(
//        p=>{
//          p.foreach(r=>{
//            KafkaProducerPool.sendmsg("qcd_gn_app",r.getString(1))
//        })
//      })
//    })
//
//    ssc.start()
//    ssc.awaitTermination()
//  }
//}
//object SQLContextSingleton {
//  @transient private var instance: SQLContext = null
//
//  // Instantiate SQLContext on demand
//  def getInstance(sparkContext: SparkContext): SQLContext = synchronized {
//    if (instance == null) {
//      instance = new SQLContext(sparkContext)
//    }
//    instance
//  }
}
