import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 家庭圈计算
  * 输入：两两成员关系(成员之两两个关系对是通过业务特征以及其它业务特征分析得出)
  * 输出：所有成员之间最多经过一个中间成员即可相连则算一个家庭圈，一个成员可以存在于多个家庭圈
  * 家庭编码，F1+10位数字
  *
  * V0.3：只计算原始关系对中标注的，通过中间节点的不考虑
  */
object SocialCircle4S3 {
  def main(args: Array[String]): Unit = {

    val sourcePath = args(0)
    val targetPath = args(1)
    val partitions = args(2).toInt

    val conf = new SparkConf()
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      //.setMaster("local[5]")
      .setAppName("SocialCircle4S2")
    val sc = new SparkContext(conf)
    //val data = sc.textFile("data/social_circle_uniq_sample.txt").filter(!_.isEmpty).filter(!_.startsWith("#")).map(line => {
    val data = sc
      .textFile(sourcePath)
      //.textFile("data/social_circle_uniq.data.gz")
      .filter(!_.isEmpty)
      .map(line => {
        val tmp = line.split(",")
        (tmp(0).trim.toLong, tmp(1).trim.toLong)
      }).filter(kv => {
      kv._1 != kv._2
    }).flatMap(kv => Array((kv._1, kv._2), (kv._2, kv._1))) //.distinct() //A<->B双向关系


    //TODO：按手机号分区？
    val kvRdd = data
      .groupByKey(partitions)
      .map(kv => {
        (kv._1, (kv._2.toSet[Long] + kv._1))
      })
      //过滤只有一个直接连接节点的节点(关系是又向的，只删除一个)
      .filter(_._2.size > 2)
      //.sample(false, 0.01)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)



    kvRdd.map(line=>line._1 + "|" + line._2.mkString("|")).coalesce(partitions/10).saveAsTextFile(targetPath + "_kv")
//    val r = kvRdd.cartesian(kvRdd)
    //NOTE:笛卡尔集,所以判断的时候只判断一个方向
//    val circle = r.map({ case (a: (Long, Set[Long]), b) => {
//      if (a._2.contains(b._1)) { //A,B直接相连
//        if (a._2.subsetOf(b._2)) { //A,B直接相连 且 B和A的所有直连节点都直连
//          (a._2 ++ b._2) //.toSeq.sortWith(_ < _)
//        } else { //A,B直接相连 但 B和A的所有直连节点不全直连
//          //(a._2 + b._1).toSeq.sortWith(_ < _)
//          a._2 //.toSeq.sortWith(_ < _)
//        }
//      } else {
//        if ((a._2 -- b._2) == Set(a._1)) { //A,B不直连，但B和A的所有直接连节点直连
//          (a._2 + b._1) //.toSeq.sortWith(_ < _)
//        } else {
//          a._2 //.toSeq.sortWith(_ < _)
//        }
//      }
//    }
//    }).filter(!_.isEmpty).coalesce(partitions).persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//
//    circle.map(_.mkString("|")).saveAsTextFile(targetPath + "_nouniq")
//    kvRdd.unpersist()

    //FIXME:合并子集
    val circle=kvRdd.map(_._2)
    val rust = circle.cartesian(circle)
    val fr = rust.flatMap({
      case (a, b) => {
        if (a == b) {
          Array((a, 0))
        } else if (a.subsetOf(b)) {
          Array((a, 1), (b, 0))
        } else if (b.subsetOf(a)) {
          Array((a, 0), (b, 1))
        } else {
          Array((a, 0), (b, 0))
        }
      }
    })

    val ff = fr.reduceByKey(_ + _).filter(_._2 == 0)
    ff.map(_._1.toSeq.sortWith(_ < _)).map(_.mkString("|")).coalesce(partitions/10).saveAsTextFile(targetPath)
    //    val fw = new FileWriter("data/rr.txt")
    //    ff.saveAsTextFile("")
    //    ff.map(_._1.toSeq.sortWith(_ < _))
    //      .collect()
    //      .foreach(line => fw.write(line.mkString("|") + "\n"))
    //    fw.close()
    //TODO写入Hive,表结构：fid,p_no
    sc.stop()
  }
}
