import org.apache.spark.{SparkConf, SparkContext}

/**
  *
  * 计算共同好友
  *
  * @author migle on 2017/8/25.
  *
  */
object CommonFriends {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("common friends").setMaster("local[2]")
    val sc = new SparkContext(conf)
    //A->()
    val data = sc.textFile("data/friends.txt").flatMap(line => {
      val tmp = line.split(":")
      //A->B是好友是，B->A也是好友,(A->[B,C])
      tmp(1).split(",").flatMap(f => Array((tmp(0), f), (f, tmp(0))))
    })

    val t2 = data.groupByKey(10).flatMap(m => {
      val cf = m._2.toArray.sorted.distinct
      //(C-B -> A)  C和B共同好友A,"C-B"为key,
      val rr = cf.flatMap(a => cf.filter(!_.eq(a)).map(b => (s"$a-$b" -> m._1) ))
      rr
    }).groupByKey().sortByKey(/*numPartitions = 1*/).foreach(r => {
      println(r._1 + ":" + r._2.toArray.sorted.mkString(","))
    })
  }
}
