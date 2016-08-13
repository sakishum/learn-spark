
import java.util.{HashMap}
import collection.JavaConverters._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import redis.clients.jedis.Jedis;
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



    val rules =List(new Rule("payment_fee ge 30"),new Rule("payment_fee le 10", "guser1"),new Rule("payment_fee gt 5", "guser1"),new Rule("payment_fee eq 10", "guser1"))
    val r = rules.map(r=>r.rule(data.toMap.asJava,jedis)) .filter(e=>e != null)

    //Producer<String, String> producer = new KafkaProducer<>(props);
    //producer.send(new ProducerRecord<String, String>("kafkatest", "hello kafka "));
    //for(int i = 0; i < 100; i++)
    //producer.send(new ProducerRecord<String, String>("kafkatest", Integer.toString(i), Integer.toString(i)));

    r.foreach(out=>
      {
        val props = new HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Conf.kafka)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")

        val producer = new KafkaProducer[String, String](props)
        out.output(producer)
        //producer.close()
      })

    println("xxxxxxxx")

    //println(r)

  }
}