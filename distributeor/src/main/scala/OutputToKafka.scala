import java.util
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.JavaConverters._
import com.asiainfo.Conf;
import com.asiainfo.rule.Rule;

/**
  * Created by migle on 2016/8/12.
  */
object OutputToKafka {
  def main(args: Array[String]) {
    //一个topic启一个app
    val consumerFrom = Set(Conf.consume_topic_netpay)
    val brokers  = Conf.kafka
    val sparkConf = new SparkConf().setAppName("KafkaStreamDist").setMaster("local[2]") //.setMaster("spark://vm-centos-00:7077")
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers, "group.id" -> Conf.groupid)

    val messageHandler = (mmd: MessageAndMetadata[String, String]) => (mmd.topic, mmd.key, mmd.message)
    // 每次启动的时候默认从Latest offset开始读取，或者设置参数auto.offset.reset="smallest"后将会从Earliest offset开始读取
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, consumerFrom)
    val data = messages.map(x => x._2).map(x => {
      //TODO:与华为的接口格式未定，姑且认为每个事件的数据是不同的topic,且字段用"|"分隔
      consumerFrom.head match {
        case Conf.consume_topic_netpay => {
          val a = x.split("\\|")
          Map("phone_no"->a(0), "date" -> a(1))
        }
        case Conf.consume_topic_usim => {
          val a = x.split("\\|")
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
      rdd.foreachPartition(p => {
        p.foreach(line => {
          import redis.clients.jedis.Jedis
          //初始化redis连接  TODO:连接池
          val jedis = new Jedis("192.168.99.130");
          jedis.auth("redispass");
          //拉取生效规则,规则在redis中缓存
          //tips:后续如果规则太多的话放在不同的key中
          //val rules = Set(new Rule("payment_fee eq 10"), new Rule("payment_fee ge 30"))
          val rules = jedis.smembers(Conf.redis_rule_key).asScala.map(r=>new Rule(r)).filter(r=>{
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
