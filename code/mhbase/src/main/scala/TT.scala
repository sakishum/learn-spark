/**
 * Created by migle on 2016/8/10.
 */
object TT {
  def main(args: Array[String]): Unit = {
    (1 to 20).foreach { messageNum =>
      val str = (1 to 10).map(x => scala.util.Random.nextInt(10).toString)
        .mkString(" ")
      println(str)
    }
  }
}
