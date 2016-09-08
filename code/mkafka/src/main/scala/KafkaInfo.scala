import kafka.utils.ZkUtils
import org.I0Itec.zkclient.ZkClient

//import com.aistream.kafka.SecurityUtils

object KafkaInfo{
  def main(args: Array[String]): Unit = {
    //SecurityUtils.securityPrepare()
    //"vm-centos-00:2181"
    //val zk = new ZkClient(args(0))
    val zk = new ZkClient("vm-centos-00:2181")


    val topics = ZkUtils.getAllTopics(zk)

    println("--topics----")
    topics.foreach(println)

    val tp = ZkUtils.getPartitionsForTopics(zk,topics)

    println("--topics && partitions----")
    tp.foreach(tp =>{
      tp._2.foreach{p =>
        println(tp  +  " " + p )
      }
    })

    //println("--broker--")
    //ZkUtils.getAllBrokersInCluster(zk).foreach(println)





//    while (true){
//      var cmd = Console.readLine();
//      cmd match {
//        case "listtopic" => ZkUtils.getAllTopics(zk).foreach(println)
//        case "brokers" => ZkUtils.getAllBrokersInCluster(zk).foreach(println)
//        case "quit" => sys.exit(0)
//        case _  => println("listtopic \t brokers")
//      }
//
//    }


  }
}