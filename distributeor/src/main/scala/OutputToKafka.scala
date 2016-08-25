package com.asiainfo.spark

import com.asiainfo.Conf
import com.asiainfo.common.ReidsPool
import com.asiainfo.rule.Rule
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.JavaConverters._
/**
  * Created by migle on 2016/8/12.
 *  每次处理一个topic,数据从kafka的最新offset开始
  */
object OutputToKafka {
  def main(args: Array[String]) {
    //一个topic启一个app
    val consumerFrom = Set(Conf.consume_topic_netpay)
    val brokers  = Conf.kafka
    val sparkConf = new SparkConf().setAppName("KafkaStreamDist") //.setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "group.id" -> Conf.groupid)
    // 每次启动的时候默认从Latest offset开始读取，后续如果有特殊需求再说
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, consumerFrom)
    println("=============================++++++++++++++++++++++++++++++")
    val data = messages.map(x => x._2).map(x => {
      println("XXXXXXXXXXXXXXXXXXXXXXXX")
      //TODO:与华为的接口格式未定，姑且认为每个事件的数据是不同的topic,且字段用"|"分隔
      consumerFrom.head match {
        case Conf.consume_topic_usim => {
          val a = x.split("\\|")
          Map("phone_no"->a(0), "date" -> a(1))
        }
        case Conf.consume_topic_netpay => {
          val a = x.split("\\|")
          println("===========================")
          println(x)
          println("===========================")
          Map("phone_no"->a(0), "payment_fee" -> a(1), "login_no" -> a(2), "date" -> a(3))
        }
        case Conf.consume_topic_order => {
          val a = x.split("\\|")
          Map("phone_no"->a(0), "prod_prcid" -> a(1),"date" -> a(2 ))
        }
        case _ => Map[String,String]()
      }
    }).filter(!_.isEmpty)
    //源数据格式解析完毕,判断规则发送数据
    data.foreachRDD(rdd => {
      //TODO:是不是可以将规则周期性的广播???
      //TODO:是不是在这里保存一下OFFSET
      rdd.foreachPartition(p => {
        p.foreach(line => {
          val jedis = ReidsPool.pool.getResource;
          //拉取生效规则,规则在redis中缓存
          val rules =  jedis.hgetAll(Conf.redis_rule_key).asScala.map(x =>{new Rule(x._2)}).filter(r=>{
            //来源数据与规则的匹配判断，tips:后续如果规则太多的话放在不同的key中就不需要在这里面判断了
            //规则让规则来判断
            consumerFrom.head match {
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
          jedis.close()
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
