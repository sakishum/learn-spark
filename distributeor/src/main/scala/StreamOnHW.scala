import java.util.Date

import com.asiainfo.Conf
import com.asiainfo.common.ReidsPool
import com.asiainfo.rule.Rule
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * Created by migle on 2016/8/26.
 * 每次处理一个topic,数据从kafka的最新offset开始
 * 网厅缴费、4g换卡、业务订购三个事件的处理
 *
 */
object StreamOnHW {
  def main(args: Array[String]) {
    if(args.length<1){
      System.err.println("please input topic")
      System.exit(-1)
    }
    //一个topic启一个app

    if(!(args(0).equals(Conf.consume_topic_netpay)||args(0).equals(Conf.consume_topic_order)||args(0).equals(Conf.consume_topic_usim))){
      System.err.println(s"only support three topics: ${Conf.consume_topic_netpay}\ ${Conf.consume_topic_order}  \ ${Conf.consume_topic_usim}")
      System.exit(-1)
    }

    val consumerFrom = Set(args(0))
    val sparkConf = new SparkConf().setAppName("AiQcdEvent")
    val ssc = new StreamingContext(sparkConf, Seconds(60*5))
    val topicMap = consumerFrom.map((_, 1)).toMap
    val messages = KafkaUtils.createStream(ssc,Conf.zkhosts,Conf.groupid,topicMap);
    val log =  LoggerFactory.getLogger("StreamOnHW")

    log.info("=====================STREAMONHW===============================")

    //KafkaUtils.createDirectStream()  在华为的平台上会报错估计是版本兼容问题
    val data = messages.map(x => {
      log.info("-----------------printmsg------------------")
      log.info(x._2)
      log.info("-----------------printmsg------------------")
      x._2}
    ).map(x => {
      //TODO:时间字段是与取系统时间呢还是华为加？
      //每个事件的数据是不同的topic,且字段用"|"分隔
      if(Some(x).isEmpty){  Map[String,String]()}
      else{
        consumerFrom.head match {
          case Conf.consume_topic_usim => {
            val a = x.split("\\|")
            Map("phone_no"->a(0), "date" -> Conf.sf.format(new Date()))
          }
          case Conf.consume_topic_netpay => {
            println(x)
            val a = x.split("\\|")
            Map("phone_no"->a(0), "payment_fee" -> a(1), "login_no" -> a(2), "date" -> Conf.sf.format(new Date()))
          }
          case Conf.consume_topic_order => {
            val a = x.split("\\|")
            Map("phone_no"->a(0), "prod_prcid" -> a(1),"date" -> Conf.sf.format(new Date()))
          }
          case _ => Map[String,String]()
        }}
    }).filter(!_.isEmpty)

    //源数据格式解析完毕,判断规则发送数据
    data.foreachRDD(rdd => {
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
          //rules.foreach(println);
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
