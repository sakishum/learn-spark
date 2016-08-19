import com.asiainfo.Conf
import com.asiainfo.rule.Rule
import redis.clients.jedis.JedisPool
import scala.collection.JavaConverters._

/**
 * Created by migle on 2016/8/17.
 */
object RedisTest {
  def main(args: Array[String]) {
    import redis.clients.jedis.Jedis
    //val consumerFrom = Set(Conf.consume_topic_netpay)
    val consumerFrom = Set(Conf.consume_topic_netpay)
    //初始化redis连接  TODO:连接池
    val jedis = new Jedis("192.168.99.130");
    jedis.auth("redispass");
    //拉取生效规则,规则在redis中缓存
    //tips:后续如果规则太多的话放在不同的key中
    //val rules = Set(new Rule("payment_fee eq 10"), new Rule("payment_fee ge 30"))
    val rules =  jedis.hgetAll(Conf.redis_rule_key).asScala.map(x =>{new Rule(x._2)}).filter(r=>{
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

    rules.foreach(r=>println(r.toString));
  }

  def redispool():Unit={
    val jpool = new JedisPool()

  }

}
