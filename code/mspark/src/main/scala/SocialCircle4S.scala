import java.io.FileWriter

import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 家庭圈计算
  * 输入：两两成员关系(成员之两两个关系对是通过业务特征以及其它业务特征分析得出)
  * 输出：所有成员之间最多经过一个中间成员即可相连则算一个家庭圈，一个成员可以存在于多个家庭圈
  */
object SocialCircle4S {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[5]").setAppName("SocialCircle")
    val sc = new SparkContext(conf)
    //val data = sc.textFile("data/social_circle_uniq_sample.txt").filter(!_.isEmpty).filter(!_.startsWith("#")).map(line => {
    val data = sc.textFile("data/social_circle_uniq.data.gz").filter(!_.isEmpty).map(line => {
      val tmp = line.split(" ")
      (tmp(0).trim.toLong, tmp(1).trim.toLong)
    }).filter(kv => {
      kv._1 != kv._2
    }).flatMap(kv => Array((kv._1, kv._2), (kv._2, kv._1))) //A<->B双向关系


    //TODO：按手机号分区？
    val kvRdd = data
      .groupByKey(10)
      .map(kv => {
        (kv._1, (kv._2.toSet + kv._1))
      })
      //过滤只有一个直接连接节点的节点(关系是又向的，只删除一个)
      .filter(_._2.size > 2)
      .sample(false, 0.1)
      .persist(StorageLevel.MEMORY_AND_DISK)
    println("数据量："+data.count())
    val r = kvRdd.cartesian(kvRdd).persist(StorageLevel.MEMORY_AND_DISK)
kvRdd.join()
    //NOTE:笛卡尔集,判断的时候只判断一个方向
    val circle = r.map({ case (a, b) => {
      if (a._2.contains(b._1)) { //A,B直接相连
        if (a._2.subsetOf(b._2)) { //A,B直接相连 且 B和A的所有直连节点都直连
          (a._2 ++ b._2) //.toSeq.sortWith(_ < _)
        } else { //A,B直接相连 但 B和A的所有直连节点不全直连
          //(a._2 + b._1).toSeq.sortWith(_ < _)
          a._2 //.toSeq.sortWith(_ < _)
        }
      } else {
        if ((a._2 -- b._2) == Set(a._1)) { //A,B不直连，但B和A的所有直接连节点直连
          (a._2 + b._1) //.toSeq.sortWith(_ < _)
        } else {
          a._2 //.toSeq.sortWith(_ < _)
        }
      }
    }
    }).filter(!_.isEmpty).distinct().persist()

    //FIXME:合并子集
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
    val fw = new FileWriter("data/rr.txt")
    ff.map(_._1.toSeq.sortWith(_ < _)).collect().foreach(line => fw.write(line.mkString("|") + "\n"))
    fw.close()

    sc.stop()
  }
}
