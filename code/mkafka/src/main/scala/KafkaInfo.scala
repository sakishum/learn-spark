import com.aistream.kafka.SecurityUtils
import kafka.utils.ZKStringSerializer
import org.I0Itec.zkclient.ZkClient

//import com.aistream.kafka.SecurityUtils
class KafkaInfo(zkString:String){
  val zk = new ZkClient(zkString,30000, 30000, ZKStringSerializer)
  def getLogSize(): Unit ={
  }
}
object KafkaInfo{
  def main(args: Array[String]): Unit = {
    SecurityUtils.securityPrepare()

    //每分钟记录一次offset
    //1. 记录数据量
    //2. partition分布是否均匀



    //"vm-centos-00:2181"
    //val zk = new ZkClient(args(0))
    //val zk = new ZkClient("vm-centos-00:2181",30000, 30000, ZKStringSerializer)
    val topics = Set("topic_lte_http")
    val kafkaparam = Map("bootstrap.servers" -> args(1))

    val kafka = new SimpleAPIConsumer(kafkaparam)

    val tp = kafka.getPartitions(topics).right
    kafka.getLatestLeaderOffsets(tp.get).right.foreach(m=>m.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset))))
    println("--"*30)
    kafka.getEarliestLeaderOffsets(tp.get).right.foreach(m=>m.foreach(kv=>println("%s-%s:%d".format(kv._1.topic,kv._1.partition,kv._2.offset))))
  }

}