import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

/**
 * Created by migle on 2014/10/28.
 */
object LocalAKKA {

  case class HiMsg(msg: String)

  case class GoodMsg(msg: String)

  val system = ActorSystem("testakka")

  class MsgActor extends Actor with ActorLogging {
    override def preStart(): Unit = {
      println("pre start:" + self.path)
    }

    override def receive: Receive = {
      case HiMsg(msg) => log.warning(" msg:" + msg + " FROM " + sender.path + " TO  " + self.path)
        //创建一个新的Actor,注意两种方式创建的Actor的路径
        //val actor2 = system.actorOf(Props[MsgActor], name = "actor2")
        val actor2  = context.actorOf(Props[MsgActor], name = "actor2")

        actor2 ! new GoodMsg("good news")

      case GoodMsg(msg) => log.warning(" msg:" + msg + " FROM " + sender.path + " TO  " + self.path)
        //给发送者返回一个消息，发送完毕后关闭系统
        sender ! "$$$$$$$!"
        // 消息接收完毕后才会关闭系统
        system.terminate()

      case _ => log.warning("unknow msg:" + " FROM " + sender.path + " TO  " + self.path)
    }

    override def postStop(): Unit = {
      println("post stop:" + self.path)
    }

  }

  def main(args: Array[String]) {
    //val system = ActorSystem("testakka")

    //同一级别的actor的名字不能重复，且不能用$开头 
    //Actors are automatically started asynchronously when created
    val actor1 = system.actorOf(Props[MsgActor], name = "actor1")
    //val actor2 = system.actorOf(Props[MsgActor],name="actor2")
    //actor1 ! "hi! Msg Actor"

    actor1 ! new HiMsg("hi!")
    //system.shutdown()
    system.awaitTermination()
  }
}