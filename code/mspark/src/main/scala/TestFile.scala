import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by migle on 2016/9/8.
 */
object TestFile {
  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("Spark Wordcount").setMaster("local[2]")
    val sc = new SparkContext(conf);
    val rdd = sc.textFile("E:\\spark\\spark-1.5.1-bin-hadoop2.6\\CHANGES.txt")
    rdd.flatMap(line => line.split(" ")).map((_,1))

  }
}
