import kafka.api.TopicMetadataRequest
import kafka.consumer.SimpleConsumer


/**
 * Created by migle on 2016/9/8.
 */
object SimpleDemo {
  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x", "sdi_scdt_3")

    val kafkaparam = Map("bootstrap.servers" -> "vm-centos-00:9092,vm-centos-01:9092",
    "group.id"->"g1")


    val demo = new SimpleDemo(kafkaparam)
    println(demo.findLeader("sdi_scdt_x", 0).get)
    println(demo.findLeader("sdi_scdt_x", 1).get)
//
//
//    val simple = new SimpleConsumer("vm-centos-00", 9092, 100000,
//      64 * 1024, "g2")
//
//    val tmr = new TopicMetadataRequest(TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, Seq())
//    val tmresp = simple.send(tmr)
//    tmresp.topicsMetadata.foreach(tm => {
//      tm.partitionsMetadata.foreach(pm => {
//        //topic,partition,leader,replicas,isr
//        println("topic:" + tm.topic + pm.toString())
//      })
//    })
//
//
//    println("simple consumer client")
//
//    val req = new FetchRequestBuilder()
//      .clientId("gx")
//      .addFetch("sdi_scdt_x", 0, 0L, 200000000)
//      .build()
//
//    val resp = simple.fetch(req)
//    println(resp.messageSet("sdi_scdt_x", 0).size)
//    resp.messageSet("sdi_scdt_x", 0).foreach(messageAndOffset => {
//      println(messageAndOffset.offset)
//      val payload = messageAndOffset.message.payload;
//      payload.limit()
//      val bytes = new Array[Byte](payload.limit);
//      payload.get(bytes);
//      System.out.println(new String(bytes, "UTF-8"));
//    })
    //simple.send()

  }
}

class SimpleDemo(val kafkaParams: Map[String, String]) {

  val brokers = kafkaParams.get("metadata.broker.list")
    .orElse(kafkaParams.get("bootstrap.servers"))
    .getOrElse(throw new Exception(
      "Must specify metadata.broker.list or bootstrap.servers")).split(",").map(x => {
    val hp = x.split(":")
    (hp(0), hp(1).toInt)
  })


  //def findLeader(topic: String, partition: Int):Either[Unit,(String,Int)] = {
  def findLeader(topic: String, partition: Int):Option[(String,Int)] = {
    withBrokers(brokers) {
      consumer => {
        val tmr = new TopicMetadataRequest(TopicMetadataRequest.CurrentVersion, 0, TopicMetadataRequest.DefaultClientId, Seq(topic))
        val resp = consumer.send(tmr)
        resp.topicsMetadata.find(_.topic == topic).flatMap(tm => {
          tm.partitionsMetadata.find(_.partitionId == partition)
        }).foreach(pm => {
          pm.leader.foreach(hp => {
            return Some(hp.host, hp.port)
          })
        })
      }
    }
    None
  }

  private def withBrokers(brokers: Iterable[(String, Int)])(fn: SimpleConsumer => Any): Unit = {
    brokers.foreach(broker => {
      var consumer: SimpleConsumer = null;
      try {
        consumer = new SimpleConsumer(broker._1, broker._2, 100000,
          64 * 1024, kafkaParams.get("group.id").getOrElse("default.group"))
        fn(consumer)
        // catch
      } finally {
        consumer.close
      }
    })
  }
}
