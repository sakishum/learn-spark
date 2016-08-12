import java.util.HashMap

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import scala.util.Random

/**
 * Created by migle on 2016/8/10.
 */
object KafkaProducer {

  def main(args: Array[String]) {
//    if (args.length < 4) {
//      System.err.println("Usage: KafkaWordCountProducer <metadataBrokerList> <topic> " +
//        "<messagesPerSec> <wordsPerMessage>")
//      System.exit(1)
//    }
    val param=Array(Conf.kafka,"10","20");
    val Array(brokers, messagesPerSec, wordsPerMessage) = param
    val topics=Array("topic-1","topic-2")

    // Zookeeper connection properties
    val props = new HashMap[String, Object]()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    // Send some messages
    while(true) {
      (1 to messagesPerSec.toInt).foreach { messageNum =>
        val str = (1 to wordsPerMessage.toInt).map(x => scala.util.Random.nextInt(10).toString)
          .mkString(" ")

        val topic=topics(Random.nextInt(2));
        println(topic);
        println(str);
        val message = new ProducerRecord[String, String](topic, null, str)

        producer.send(message)
      }

      Thread.sleep(1000)
    }
  }
}
