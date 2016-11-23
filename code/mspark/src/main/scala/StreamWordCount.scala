import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/6/30.
 */
object StreamWordCount {
  def main(args: Array[String]) {
    val conf = new SparkConf()
      .setAppName("SparkStreaming Wordcount")
      .setMaster("local[2]")
      //.set("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6")
      //.setMaster("spark://vm-centos-00:7077")
      //.set("spark.executor.memory", "100m")
      //.set("spark.testing", "10")

    val ssc = new StreamingContext(conf, Seconds(5))
   // ssc.sparkContext.addJar("E:\\workspace\\learn-spark\\code\\mspark\\target\\mspark-1.0-SNAPSHOT.jar")
    //val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
    val lines = ssc.socketTextStream("localhost", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    //lines.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b).print()
    //.collect().foreach(t => println(t._1 + ":" + t._2))
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
    wordCounts.print()
    println("=======================================")
    ssc.start()
    ssc.awaitTermination()

  }
}
