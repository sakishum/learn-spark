/**
 * Created by migle on 2016/9/8.
 */

import java.util.Properties
import java.util.concurrent.{CountDownLatch, TimeUnit, Executors}

import kafka.consumer.{ConsumerConfig, KafkaStream}

object ConsumerDemo {
 private[this] val downLatch = new CountDownLatch(10);
  def main(args: Array[String]) {
    val props = new Properties()
    props.put("zookeeper.connect", "vm-centos-01:2181")
    props.put("group.id", "g2")
    props.put("zookeeper.session.timeout.ms", "15000")
    props.put("zookeeper.sync.time.ms", "2000")
    props.put("auto.commit.interval.ms", "1000")
    //props.put("auto.offset.reset", "smallest")
    val consumer = kafka.consumer.Consumer.create(new ConsumerConfig(props))


    val mtot = Map("sdi_scdt_3" -> 1, "sdi_scdt_4" -> 2, "sdi_scdt_5" -> 1)

    println(mtot.values.sum)
    val tstreams = consumer.createMessageStreams(mtot)

    val executorpool = Executors.newFixedThreadPool(mtot.values.sum)
      try{
        tstreams.values.foreach {
          _.foreach {
            stream => {
              executorpool.submit(new MessageHandler(stream))
            }
          }
        }
      }finally {
        executorpool.shutdown()
        //executorpool.awaitTermination(5000, TimeUnit.MILLISECONDS)
      }


    downLatch.await()

    println("关闭consumer")
    //程序异常结束是不是会有offset没有提交?
    consumer.shutdown()
    println("运行结束")
  }

  private class MessageHandler(stream: KafkaStream[Array[Byte], Array[Byte]])
    extends Runnable {
    def run() {
      try {
        val streamIterator = stream.iterator()
        while (streamIterator.hasNext()) {
          println("------------------------------------")
          downLatch.countDown()
          val msgAndMetadata = streamIterator.next()
          println("------------------------------------")
          println(msgAndMetadata.topic)
          println(new String(msgAndMetadata.message()))
          println(msgAndMetadata.offset)


        }
      } catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

}
