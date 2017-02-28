/**
  * Created by migle on 2016/12/9.
  */
class GetterAndSetter() {
 var name:String="hello"
}
object XXXX{
  def main(args: Array[String]): Unit = {
   val sg =  new GetterAndSetter
    sg.name="hello scala"
    println(sg.name)
  }
}