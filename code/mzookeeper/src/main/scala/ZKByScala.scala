import org.apache.zookeeper.{WatchedEvent, Watcher, ZooKeeper}
import org.apache.zookeeper.data.Stat

object ZKByScala{
  def main(args: Array[String]): Unit = {
    val  zk = new ZooKeeper("localhost:2181",2000, new Watcher() {
      def process(event: WatchedEvent) {
        System.out.println("发生了!：" + event.getType + "事件！")
      }
    })
    val stat = new Stat();
    val b = zk.getData("/zktest",true,stat)
    println(new String(b))
    println(stat.getAversion)
    println(stat.getCtime)
    println(stat.getDataLength)
    println(stat.getMtime)
  }
}