;
/**
 * Created by migle on 2016/8/17.
 */
object ScalaLang {
  def main(args: Array[String]) {
//    val jedis = new Jedis("192.168.99.130");
//    jedis.auth("redispass");
//    val rstr = jedis.smembers(Conf.redis_rule_key).asScala;
//    rstr.foreach(println)
//    println("==============================================")
//    val data = Map("phone_no"-> "18797384480","payment_fee"->"100","login_no"->"m001","date"->"2016-08-01")
//    rstr.map(str => new Rule(str)).foreach(r=>println(r.rule(data.asJava,jedis)));
//
////    val set = Set("t1","t2","t3")
//  val topic=Set("topic");
//    val data = topic.head match {
//      case "topic" => Map("a"->"b")
//      case "topic-1" => Map("c"->"d","e"->"f")
//      case _ => Map()
//    }
//
//    println(data)


    val x = Map("c"->"d","e"->"f")
    println(x.mkString("{",",","}"))
    //print(x.getClass.getName)
  }
}