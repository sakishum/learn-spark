package graphx

import org.apache.spark._
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD


object SocialCircle {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SocialCircle").setMaster("local[5]")
    val sc = new SparkContext(conf)
    val data = sc.textFile("data/social_circle_sample.data").filter(!_.isEmpty).map(line => {
      val tmp = line.split(" ").map(_.toLong)
      if (tmp(0)<= tmp(1)){
        (tmp(0), tmp(1))
      } else{
        (tmp(1),tmp(0))
      }   //排序，大的在后，小的在前,方便后面计算三角形
    }) //.flatMap(kv => Array(kv, (kv._2, kv._1))).cache()  //在数据上直接生成双向

    //val users = data.flatMap(_.split(" "))
    //val users: RDD[(VertexId, (String, String))] =
    //[ID,（属性)]
    val users: RDD[(VertexId, (String))] = data.flatMap(
      { line => Array((line._1, ("no:" + line._1.toString)), (line._2, ("no:" + line._2.toString))) }
    )
    val relationships: RDD[Edge[String]] = data.map(line => {
      Edge(line._1, line._2, "family")
    })

    //users.sortByKey().map(_._1).foreach(println)
    //val graph = GraphLoader.edgeListFile(sc, "data/social_circle_sample.data")

    val graph = Graph(users, relationships)

    //    println(graph.numEdges)
    //    println(graph.numVertices)
    //    graph.vertices.foreach(x=>println(x._1+":"+x._2))
    //triangleCount()计算三通过每个顶点的三角形数量
    //一个顶点有两个相邻的顶点以及相邻顶点之间的边时，这个顶点是一个三角形的一部分。GraphX在TriangleCount object 中实现了一个三角形计数算法，它计算通过每个顶点的三角形的数量。需要注意的是，在计算社交网络数据集的三角形计数时，TriangleCount需要边的方向是规范的方向(srcId < dstId), 并且图通过Graph.partitionBy分片过
//    graph.partitionBy(PartitionStrategy.RandomVertexCut)
//      .triangleCount()
//      .collectNeighborIds(EdgeDirection.Either).foreach(k=>println(k._1+":"+k._2.mkString("|")))
    graph.triangleCount().vertices.foreach(println)
    //graph.triangleCount().triplets.foreach(println)
    //graph.triangleCount().edges.foreach(println)



    //connectedComponents()用来求连通图，结果是一个 是一个tuple类型，key分别为所在的顶点id，value为key所在的连通体id(连通体中顶点id最小值)
    //graph.connectedComponents().vertices.foreach(println)
    //所有结点互相连通
    //graph.stronglyConnectedComponents(30).vertices.foreach(println)
    //graph.connectedComponents().edges.foreach(println)
    //println(graph.connectedComponents().vertices.collect().mkString("|"))
    //    graph.triangleCount().triplets.map(_.toTuple).foreach(x=>println(x.toString()))
    //graph.triangleCount().collectNeighborIds(EdgeDirection.Either).foreach(kv=>println(kv._1 +":" + kv._2.distinct.mkString("|")))
    //println(graph.triplets.count())

//    val tc = graph.triangleCount()

//    tc.vertices.collect
//    println("Triangle counts: " + graph.connectedComponents.triangleCount().vertices.top(5).mkString("\n"));

//    val sum = tc.vertices.map(a => a._2).reduce((a, b) => a + b)
    //计算入度
//    graph.inDegrees.foreach(x=>println(x.toString()))
//    graph.collectNeighborIds(EdgeDirection.Either).foreach(kv=>println(kv._1 +":" + kv._2.distinct.mkString("|")))
    //graph.triplets.map(kv=>kv)
  }
}
