/**
 * Created by migle on 2016/8/10.
 */
object TT {
  def main(args: Array[String]): Unit = {
//    (1 to 20).foreach { messageNum =>
//      val str = (1 to 10).map(x => scala.util.Random.nextInt(10).toString)
//        .mkString(" ")
//      println(str)
//    }
val sg =  new GetterAndSetter
    sg.name="hello scala"
    println(sg.name)

    val a=Array(1,2,3,4,5)
      implicit def ord = new Ordering[Int]{
      override def compare(x: Int, y: Int): Int = if(x>y) -1 else 1
    }

    println(a.sorted.mkString("|"))
  }
}
