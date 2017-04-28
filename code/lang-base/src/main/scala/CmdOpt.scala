import scala.collection.mutable

/**
 * Created by migle on 2016/9/2.
 */
object CmdOpt {
  def main(args: Array[String]) {
      val x =Set("aa","bb","cc")
      val m = x.map(x=>x+"!!")
    println(m.toSeq)
    val hmap = new mutable.HashMap[String,String]
    hmap.put("xx","yy")
    val r =  hmap.get("xx!") match {
      case Some(str) => str;
      case _ => "not exist"
    }

    println(r)
    println("--")
  }
}
