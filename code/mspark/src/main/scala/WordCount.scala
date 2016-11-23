import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by migle on 2016/6/20.
 */
object WordCount {
  //  if (System.getProperty("os.name").toLowerCase.startsWith("win")) {
  //    System.setProperty("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6")
  //    System.out.println("Win系统")
  //  }

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6")
    val conf = new SparkConf()
      .setAppName("Spark Wordcount")
      .set("hadoop.home.dir", "E:\\spark\\hadoop-2.6.4\\hadoop-2.6")
      .setMaster("spark://vm-centos-00:7077")
      .set("spark.executor.memory", "100m")
      .set("spark.testing", "10")
    //.setMaster("local[4]")


    val sc = new SparkContext(conf)

    //FIXME：修改成相对路径
    sc.addJar("E:\\workspace\\learn-spark\\code\\mspark\\target\\mspark-1.0-SNAPSHOT.jar")
    println("==========================sparkcontext ==========================")

    //TODO：文件是本地文件时应该在什么位置？driver?还是所有worker上?文件是如何分发的
    //val rdd = sc.textFile("/opt/spark/CHANGES.txt")
    println("连接到master，worker上的CoarseGrainedExecutorBackend也已经启动，准备执行任务？====================")
    val rdd = sc.textFile("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/CHANGES.txt")

    //val rdd = sc.textFile("/opt/spark/pom.xml")
    //文件不在hdfs上，要在每个worker上？还是driver???????

    //Note: collect()  Return all the elements of the dataset as an array at the driver program. This is usually useful after a filter or other operation that returns a sufficiently small subset of the data.
    rdd.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b) => a + b).collect().foreach(t => println(t._1 + ":" + t._2))
    //.saveAsTextFile("wordcount")
    sc.stop()
  }
}
