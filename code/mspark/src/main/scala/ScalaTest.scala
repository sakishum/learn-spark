/**
 * Created by migle on 2016/6/22.
 */
class ScalaTest {
  var _jars:Option[String] = _
  def jars: Option[String] = _jars
  _jars = Option("xxxxx")


}


object app{
  def main(args: Array[String]) {
    val st = new ScalaTest
    println(st._jars.get)
    println("===========================")
    println(st.jars.get)
  }
}
