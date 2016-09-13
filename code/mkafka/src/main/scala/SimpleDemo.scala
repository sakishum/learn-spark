import kafka.consumer.SimpleConsumer

/**
 * Created by migle on 2016/9/8.
 */
object SimpleDemo {
  def main(args: Array[String]) {
    val topics = Set("sdi_scdt_x","sdi_scdt_3")
    val simple = new SimpleConsumer(host, port, config.socketTimeoutMs,
      config.socketReceiveBufferBytes, config.clientId)
  }
}
