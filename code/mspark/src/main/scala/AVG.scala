
import org.apache.spark.rdd.RDD
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
    /**
      * createCombiner: V => C ，这个函数把当前的值作为参数，此时我们可以对其做些附加操作(类型转换)并把它返回 (这一步类似于初始化操作)
mergeValue: (C, V) => C，该函数把元素V合并到之前的元素C(createCombiner)上 (这个操作在每个分区内进行)
mergeCombiners: (C, C) => C，该函数把2个元素C合并 (这个操作在不同分区间进行)
      */
    //val  rdd2 = sc.parallelize(Array(("Fred", 88.0),("migle",98), ("Fred", 95.0), ("Fred", 91.0), ("Wilma", 93.0), ("Wilma", 95.0), ("Wilma", 98.0)))

    val rdd2 = sc.parallelize(Seq(("t1", 1), ("t1", 2), ("t1", 3), ("t2", 2),("t2", 3),("t2", 4),("t2", 5),("t3", 3)))
    //rdd2.reduceByKey(_+_).foreach(println)
    //rdd2.map(K=>(K._1,1)).reduceByKey(_+_).foreach(println)
    val data3: RDD[(String, (Int, Int))] = rdd2.combineByKey(
      (x: Int) => (x, 1),
      (pair: (Int, Int), value: Int) =>
        (pair._1 + value, pair._2+1),
      (pair1: (Int, Int), pair2: (Int, Int)) =>
        (pair1._1 + pair2._1, pair1._2 + pair2._2)
    )
      data3//.map{ case (key, value) => (key, value._1 / value._2.toFloat) }.collect()
        .foreach(println)
    println("*"*10)
    rdd2.combineByKey(x=>(x,1),(x:(Int,Int),y:Int)=>(x._1+y,x._2+1),(x:(Int,Int),y:(Int,Int))=>(x._1+y._1,x._2+y._2)).foreach(println)
  }
}
