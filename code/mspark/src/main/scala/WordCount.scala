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
    val conf = new SparkConf()
      .setAppName("Spark Wordcount")
      .setMaster("spark://vm-centos-00:7077")
    .set("spark.executor.memory","300m")
    .set("spark.testing","10")
      //.setMaster("local[4]")

    val sc = new SparkContext(conf)
    val rdd = sc.textFile("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/data1.dat")
    rdd.flatMap(line=> line.split(" ")).map(word=>(word,1)).reduceByKey((a,b)=>a+b).foreach(t=>println(t._1 + ":" + t._2))
    //.saveAsTextFile("wordcount")
    sc.stop()
  }
}
