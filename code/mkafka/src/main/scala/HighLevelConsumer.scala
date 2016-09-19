/**
 * Created by migle on 2016/9/8.
 */

import java.util.Properties
import java.util.concurrent.Executors

import kafka.consumer.{ConsumerConfig, KafkaStream}

/**
 * High Level Consumer API
 * 1. 会自动提交offset至zk
 * 2. 会接着在zk中消费的offset往后继续消费消息
 * 3. 也可以从头开头消费:删除zk中的offset并且有"auto.offset.reset"->"smallest"
 * 4. 也可以消费新产生的消费:删除zk中的offset并且有"auto.offset.reset"->"largest"
 */
object HighLevelConsumer {
 //private[this] val downLatch = new CountDownLatch(10);  //FIXME  读取指定条数
  def main(args: Array[String]) {
    val props = new Properties()
    props.put("zookeeper.connect", "vm-centos-01:2181")
    props.put("group.id", "g1")
    props.put("zookeeper.session.timeout.ms", "15000")
    props.put("zookeeper.sync.time.ms", "2000")
    props.put("auto.commit.interval.ms", "1000")
    props.put("auto.offset.reset", "smallest")
    val consumer = kafka.consumer.Consumer.create(new ConsumerConfig(props))

    val mtot = Map("sdi_scdt_x" -> 1) //, "sdi_scdt_4" -> 2, "sdi_scdt_5" -> 1
    val tstreams = consumer.createMessageStreams(mtot)

    val executorpool = Executors.newFixedThreadPool(mtot.values.sum)

        tstreams.values.foreach {
          _.foreach {
            stream => {
              executorpool.submit(new MessageHandler(stream))
            }
          }
        }
        //downLatch.await()
        //executorpool.shutdown()
        //executorpool.awaitTermination(0, TimeUnit.MILLISECONDS)
        //consumer.shutdown()
    println("运行结束")
  }

  private class MessageHandler(stream: KafkaStream[Array[Byte], Array[Byte]])
    extends Runnable {
    def run() {
      try {
        val streamIterator = stream.iterator()
        while (streamIterator.hasNext()) {

          val msgAndMetadata = streamIterator.next()
          println("%s:%-5d:%s".format(msgAndMetadata.topic,msgAndMetadata.offset,new String(msgAndMetadata.message())))
          //downLatch.countDown()
        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

}
