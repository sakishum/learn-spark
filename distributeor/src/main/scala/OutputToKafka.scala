import java.util

import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import collection.JavaConverters._
import com.asiainfo.Conf;
import com.asiainfo.rule.Rule;
/**
  * Created by migle on 2016/8/12.
  */
object OutputToKafka {
  def main(args: Array[String]) {
    val consumerFrom = Set("topic-1")

    val  brokers  = Conf.kafka
    val sparkConf = new SparkConf().setAppName("KafkaStreamDist").setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    val kafkaParams = Map[String, String]("metadata.broker.list" -> Conf.kafka, "group.id" -> Conf.groupid)

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, consumerFrom)

    //TODO 应该根据topic为选择格式
    val toData = messages.map(x => x._2).map(x => {
      val a = x.split("\\|")
      Map("phone_no"->a(0), "payment_fee" -> a(1), "login_no" -> a(2), "date" -> a(3))
//      val h = new util.HashMap[String, String]()
//      h.put("phone_no", a(0))
//      h.put("payment_fee", a(1))
//      h.put("login_no", a(2))
//      h.put("date", a(3))
//      h
    })
    //toData.print()

    //    toData.foreachRDD(rdd=>{
    //      rdd.map(m=>m.get("payment_fee")).foreach(println)
    //    })

    //源数据格式解析完毕,判断规则发送数据
    toData.foreachRDD(rdd => {
      rdd.foreachPartition(p => {
        p.foreach(line => {
          import redis.clients.jedis.Jedis
          //初始化redis连接  TODO:连接池
          val jedis = new Jedis("192.168.99.130");
          jedis.auth("redispass");
          //拉取生效规则,规则在redis中缓存
          val rules = Set(new Rule("payment_fee eq 10"), new Rule("payment_fee ge 30"))

          //规则判断,生成最终结果
          val data = rules.map(rule => rule.rule(line.toMap.asJava, jedis)).filter(e=>e.hasData)

          //将最终结果写入kafka
          data.foreach(d => {

            //TODO 连接池
            val props = new util.HashMap[String, Object]()
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
              "org.apache.kafka.common.serialization.StringSerializer")
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
              "org.apache.kafka.common.serialization.StringSerializer")
            val producer = new KafkaProducer[String, String](props)
            //根据规则放入规定的topic
            d.output(producer)
          })
        })
      })
    })
    ssc.start();
    ssc.awaitTermination();
  }
}
