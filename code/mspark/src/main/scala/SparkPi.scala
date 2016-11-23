import scala.math._
import org.apache.spark._
/**
 * Created by migle on 16/6/19.
 */
/** Computes an approximation to pi */
object SparkPi {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Spark Pi").setMaster("spark://vm-centos-00:7077")

    val sc = new SparkContext(conf)
    //TODO:Mac上虚拟机是桥接模式，为什么可以直接连接调试程序？不用sc.addJar
   // sc.addJar("E:\\workspace\\learn-spark\\code\\mspark\\target\\mspark-1.0-SNAPSHOT.jar")

    val slices = if (args.length > 0) args(0).toInt else 200
    val n = math.min(100000L * slices, Int.MaxValue).toInt // avoid overflow
    val count = sc.parallelize(1 until n, slices).map { i =>
        val x = random * 2 - 1
        val y = random * 2 - 1
        if (x*x + y*y < 1) 1 else 0
      }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / n)
    sc.stop()
  }
}