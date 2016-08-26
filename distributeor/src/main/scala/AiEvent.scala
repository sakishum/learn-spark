package com.asiainfo.spark

import com.asiainfo.Conf
import com.asiainfo.common.ReidsPool
import com.asiainfo.rule.Rule
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.{KafkaTool, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.JavaConverters._
/**
 * Created by migle on 2016/8/12.
 * 每次处理一个topic,数据从kafka的最新offset开始
 */
object AiEvent {
  def main(args: Array[String]) {
    //一个topic启一个app
    val topics = Set(Conf.consume_topic_netpay,Conf.consume_topic_order,Conf.consume_topic_usim)
    val brokers  = Conf.kafka
    val sparkConf = new SparkConf().setAppName("AiEventTest")//.setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "group.id" -> Conf.groupid)
    //val offsets = KafkaOffsetTool.getLatestOffset(topics)
    //TODO
    val offsets = KafkaTool.getOffsets(kafkaParams,topics)
   // println("-----"*10)
   // offsets.foreach(x=>println(x._1.topic +" " + x._1.partition + " " + x._2))
   // println("-----"*10)

    val DStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String,String,String)](
            ssc, kafkaParams, offsets,
            (m: MessageAndMetadata[String, String]) => (m.topic,m.key(),m.message()))

    val data = DStream.map(tm => (tm._1,tm._3)).map(m => {
      //TODO:垃圾数据处理！！,每个事件的数据是不同的topic,且字段用"|"分隔
      println("=============================1")
      print(m)
      println("=============================1")
      if(Some(m._2).isEmpty){  Map[String,String]()}
      else{
      m._1 match {
        case Conf.consume_topic_usim => {
          val a = m._2.split("\\|")
          Map("phone_no"->a(0), "date" -> a(1),"s_topic"->m._1)
        }
        case Conf.consume_topic_netpay => {
          println("==============2")
          val a = m._2.split("\\|")
          Map("phone_no"->a(0), "payment_fee" -> a(1), "login_no" -> a(2), "date" -> a(3),"s_topic"->m._1)
        }
        case Conf.consume_topic_order => {
          val a = m._2.split("\\|")
          Map("phone_no"->a(0), "prod_prcid" -> a(1),"date" -> a(2 ),"s_topic"->m._1)
        }
        case _ => Map[String,String]()
      }
    }}
    ).filter(!_.isEmpty)




    //源数据格式解析完毕,判断规则发送数据
    data.foreachRDD(rdd => {
      //TODO:是不是可以将规则周期性的广播???
      //TODO:是不是在这里保存一下OFFSET
      rdd.foreachPartition(p => {

        p.foreach(line => {
          val jedis = ReidsPool.pool.getResource;
          //拉取生效规则,规则在redis中缓存
          val rulesData =  jedis.hgetAll(Conf.redis_rule_key)
          jedis.close()

          val rules  = rulesData.asScala.map(x =>{new Rule(x._2)}).filter(r=>{
            //来源数据与规则的匹配判断，tips:后续如果规则太多的话放在不同的key中就不需要在这里面判断了
            //规则让规则来判断
            line.get("s_topic").get match {
              case Conf.consume_topic_netpay => {
                r.getEventid.equalsIgnoreCase(Conf.eventNetpay)
              }
              case Conf.consume_topic_usim => {
                r.getEventid.equalsIgnoreCase(Conf.eventUSIMChange)
              }
              case Conf.consume_topic_order => {
                r.getEventid.equalsIgnoreCase(Conf.eventBusiOrder)
              }
              case _ => false
            }
          });


          println("===================================")
          rules.foreach(println);
          println(line.mkString("{",",","}"))
          println("===================================")
          //规则判断,生成最终结果
          val data = rules.map(rule => rule.rule(line.toMap.asJava)).filter(e=>e.hasData)
          //将最终结果写入kafka
          data.foreach(out=>out.output());
        })
      })
    })
    ssc.start();
    ssc.awaitTermination();
  }
}
