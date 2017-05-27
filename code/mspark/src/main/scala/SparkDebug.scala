import org.apache.spark.SparkEnv

/**
  * Created by migle on 2017/4/28.
  */
object SparkDebug {
  def main(args: Array[String]): Unit = {
    val m1 = (str:String) => str.toUpperCase
    print(m1("abc"))
    val ser = SparkEnv.get
    println(ser == null)

  }
}
