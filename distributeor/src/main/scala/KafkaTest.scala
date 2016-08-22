
import com.asiainfo.rule.Rule
import redis.clients.jedis.Jedis

import scala.collection.JavaConverters._;
object KafkaTest{
  def main(args:Array[String]): Unit ={
//    (1 to 10).foreach { messageNum =>
//      val str = (1 to 10).map(x => scala.util.Random.nextInt(10).toString)
//        .mkString(" ")
//      println(str)
//    }
//
//    val topics=Array("topic-1","topic-2");
//    topics.foreach(print)
    //(1 to 10).foreach(print(topics(1)));
//    val x = "hello|spark|streaming"
//    val m  = Map()
//    for(s<-x.split("\\|")){
//     //m + (s->s)
//    }

    val jedis = new Jedis("192.168.99.130");
    jedis.auth("redispass");
//    val data: Map[String, String] = new HashMap[String, String]
//    data.put("phone_no", "18797384480")
//    data.put("payment_fee", "10")
//    data.put("login_no", "m001")
//    data.put("date", "2016-08-01")
    val data = Map("phone_no"-> "18797384480","payment_fee"->"10","login_no"->"m001","date"->"2016-08-01")



    //val rules =List(new Rule("payment_fee ge 30"),new Rule("payment_fee le 10", "guser1"),new Rule("payment_fee gt 5", "guser1"),new Rule("payment_fee eq 10", "guser1"))
    val rules =List(new Rule("payment_fee ge 30"),new Rule("payment_fee le 10"),new Rule("payment_fee gt 5"),new Rule("payment_fee eq 10"))
    val r = rules.map(r=>r.rule(data.toMap.asJava)) .filter(e=>e != null)

    //Producer<String, String> producer = new KafkaProducer<>(props);
    //producer.send(new ProducerRecord<String, String>("kafkatest", "hello kafka "));
    //for(int i = 0; i < 100; i++)
    //producer.send(new ProducerRecord<String, String>("kafkatest", Integer.toString(i), Integer.toString(i)));

    r.foreach(out=>
      {
        out.output()
        //producer.close()
      })

    println("xxxxxxxx")

    //println(r)

  }
}