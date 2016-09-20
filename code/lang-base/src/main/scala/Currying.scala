import scala.util.control.NonFatal

/**
 * Created by migle on 2016/9/20.
 */
object Currying {
  def main(args: Array[String]): Unit = {
    val test = new Currying()
    println(":" + test.printHost())
  }
}

class Currying {
  val broker = List(("vm-centos-00", 9092), ("vm-centos-01", 9092), ("vm-centos-02", 9092))

  def printHost(): String = {
    withScop(broker) {
      host => {
        if (host == "vm-centos-01:9092") {
          //return "hello"
          throw new Exception("ERROR HOST:PORT")
        }
      }
    }
    "xx"
  }

  def withScop(broker: Iterable[(String, Int)])(fn: (String) => Any): Unit = {
    broker.foreach(b => {
      val host = b._1 + ":" + b._2
      try{
        fn(host)
        println(host)
      }
      catch{
        case NonFatal(e) => println(e.getMessage)
      }finally{
        println("withScop:" + host)
      }
    })
  }
}


