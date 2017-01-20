

/**
 * Created by migle on 2016/8/10.
 */
object TT {
  def main(args: Array[String]): Unit = {
//    (1 to 20).foreach { messageNum =>
//      val str = (1 to 10).map(x => scala.util.Random.nextInt(10).toString)
//        .mkString(" ")
//      println(str)
//    }
//val sg =  new GetterAndSetter
//    sg.name="hello scala"
//    println(sg.name)
//
//    val a=Array(1,2,3,4,5)
//      implicit def ord = new Ordering[Int]{
//      override def compare(x: Int, y: Int): Int = if(x>y) -1 else 1
//    }
//
//    println(a.sorted.mkString("|"))
  val m = Map[String,Long]("A-1"->10,"A-2"->20,"B-1"->10)
    val f = m.fold(("A"->0L))((a,b)=>(a._1,a._2+b._2))
    println(f)
    val g =  m.groupBy(op=>op._1.split("-")(0))  //
    println(g) //Map(A -> Map(A-1 -> 10, A-2 -> 20), B -> Map(B-1 -> 10))


  println(m.count(_=>true))
  val x=  m.reduce((a,b)=>(a._1,a._2 + b._2))
    println(x)
    val aa = summary(List(1,2,3,4,5,6,7,80000000000L))
println(aa)
     println("topic:%-18s|partition:%-3.0f|sum:%-12.0f|max:%-10.0f|min:%10.0f|avg:%-10.0f|sd:%.2f".format("xxxxx",aa("count"),
             aa("sum"),
             aa("max"),
             aa("min"),
             aa("avg"),
             aa("sd")))

  }
  def summary(list: Iterable[Long])= {
    val count = list.size
    val sum =   list.sum //list.fold(0L)((a, b) => a + b)
    val avg =   sum/count

    val m = Map[String,Double](
      "count" -> count,
      "sum"   -> sum,
      "max"   -> list.max,
      "min"   -> list.min,
      "avg"   -> avg,
      "sd"    -> sd(list)
    )
    //标准差
    m
  }

  def sd(implicit list:Iterable[Long]):Double={
    val avg = list.sum/list.size
    val s2  = list.map(x=>math.pow(x-avg,2)).sum/list.size
    math.sqrt(s2)
  }
}
