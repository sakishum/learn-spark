import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

//awk -F"," 'NR != 1 {print substr($1,1,11), substr($2,1,11)}' 圈子模型（优化版）预测结果.csv|grep -v null |sort|uniq > social_circle_uniq.data
//
//awk -F"," 'NR != 1 {print substr($1,1,11), substr($2,1,11),substr($3,1,1)}' 圈子模型（优化版）预测结果.csv |sort > sc_tmp.txt
object SocialCircle {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SocialCircle")
    val sc = new SparkContext(conf)
    val data = sc.textFile("data/social_circle_uniq_sample.txt").filter(!_.isEmpty).filter(!_.startsWith("#")).map(line => {
      //val data = sc.textFile("data/social_circle_uniq.data.gz").filter(!_.isEmpty).map(line => {
      val tmp = line.split(" ")
      (tmp(0).trim, tmp(1).trim)
    }).filter(kv => {
      kv._1 != kv._2
    }).flatMap(kv => Array((kv._1, kv._2), (kv._2, kv._1))) //A<->B双向关系


    //TODO：按手机号分区？
    val kvRdd = data
      .groupByKey()
      .map(kv => {
        (kv._1, (kv._2.toSet ++ Array(kv._1)))
      })
      .filter(_._2.size > 2) //过滤只有一个直接连接节点的节点(关系是又向的，只删除一个)
      //.sample(false, 0.1)
      .persist(StorageLevel.MEMORY_AND_DISK)

    //kvRdd元素本身也构成一个家庭圈
    //    println(rdd.count())
    //    println(kvRdd.filter(_._2.size>2).count())
    //    println(kvRdd.filter(_._2.size>2).first())
    //    println("=" * 10)
    //    kvRdd.foreach(println)
    //    println("=" * 10)

    val r = kvRdd.cartesian(kvRdd)
    r.foreach(println)
    //reduce?
    //    //FIXME:子集没有合并！
    //    val circle = r.map({ case (a, b) => {
    //      if (a._1 != b._1) {
    //        val tmp = a._2.intersect(b._2)
    //        //if (f.size >= 3 && f.contains(kv._1._1) && f.contains(kv._2._1)) {  //全连通
    //        val tt = (tmp ++ Array(a._1, b._1))
    //        if (tt.size >= 3) {
    //          tt.toSeq.sortWith(_ < _)
    //        } else {
    //          Seq()
    //        }
    //      } else {
    //        a._2.toSeq.sortWith(_ < _)
    //      }
    //    }
    //    }).filter(!_.isEmpty).distinct().persist()


    val circle = r.map(kv => {
      if (kv._1._1 != kv._2._1) {
        if (kv._1._2.subsetOf(kv._2._2)) {
          kv._2._2.toSeq.sortWith(_ < _)
        } else {
          if (kv._2._2.subsetOf(kv._1._2)) {
            kv._1._2.toSeq.sortWith(_ < _)
          } else {
            val tmp = kv._1._2.intersect(kv._2._2)
            //if (f.size >= 3 && f.contains(kv._1._1) && f.contains(kv._2._1)) {  //全连通
            val tt = (tmp ++ Array(kv._1._1, kv._2._1))
            if (tt.size >= 3 && !tt.subsetOf(kv._1._2) && !tt.subsetOf(kv._2._2)) {
              tt.toSeq.sortWith(_ < _)
            } else {
              Seq()
            }
          }
        }
      } else {
        kv._1._2.toSeq.sortWith(_ < _)
      }
    }).filter(!_.isEmpty) //.distinct().persist()

    circle.map(_.mkString("|")).foreach(println)

    //    val rr = circle.map(_.mkString("|")).collect()
    //    val fw = new FileWriter("data/rr.txt")
    //    rr.foreach(line => fw.write(line+"\n"))
    //    fw.close()
    //repartition(1).saveAsTextFile("data/rr.txt")

    //println(r.count())
    //r.foreach(println)
    // println("end.....")
    sc.stop()
  }
}
