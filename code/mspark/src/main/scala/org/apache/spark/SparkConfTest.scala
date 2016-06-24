package org.apache.spark

/**
 * Created by migle on 2016/6/22.
 */

object SparkConfTest {
  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("yarn-cluster")
    //conf.set("spark.submit.deployMode","cluster|client")

    println(conf.get("spark.master"))
//    conf.validateSettings()
//    if (conf.contains("spark.master") && conf.get("spark.master").startsWith("yarn-")){
//      conf.get("spark.master") match {
//        case "yarn-cluster" =>
//          //logWarning(warning)
//          conf.set("spark.master", "yarn")
//          conf.set("spark.submit.deployMode", "cluster")
//          println("11111")
//        case "yarn-client" =>
//          //logWarning(warning)
//          conf.set("spark.master", "yarn")
//          conf.set("spark.submit.deployMode", "client")
//          println("2222")
//        case _ => // Any other unexpected master will be checked when creating scheduler backend.
//          println("333")
//      }

//  }



  }
}
