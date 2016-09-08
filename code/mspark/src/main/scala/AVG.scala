
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by migle on 16/9/4.
  */
object AVG {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("avg").setMaster("local[*]")
    val sc  = new SparkContext(conf)
    val rdd = sc.parallelize(1 to 100)
    val data = rdd.map(x=>(x,1)).reduce((a,b)=>(a._1+b._1,a._2+b._2))
    println("avg is:" + (data._1/data._2))

    val data2 = rdd.aggregate((0,0))((a,b)=>(a._1+b,a._2+1),(x,y)=>(x._1+y._1,x._2+y._2))
    println("avg is:" + (data2._1/data2._2))

  }
}
