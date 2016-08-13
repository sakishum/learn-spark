

object KafkaTest{
  def main(args:Array[String]): Unit ={
//    (1 to 10).foreach { messageNum =>
//      val str = (1 to 10).map(x => scala.util.Random.nextInt(10).toString)
//        .mkString(" ")
//      println(str)
//    }
//
//    val topics=Array("topic-1","topic-2");
//    topics.foreach(print)
    //(1 to 10).foreach(print(topics(1)));
    val x = "hello|spark|streaming"
    val m  = Map()
    for(s<-x.split("\\|")){
     //m + (s->s)
    }


  }
}