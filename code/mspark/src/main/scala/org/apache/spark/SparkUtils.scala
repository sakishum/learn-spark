package org.apache.spark

import org.apache.spark.util.Utils

/**
 * Created by migle on 2016/6/22.
 */
object SparkUtils {
  def main(args: Array[String]): Unit = {
    println(Utils.localHostName)
    //"SPARK_LOCAL_HOSTNAME"
    Utils.setCustomHostname("migle")
    println(Utils.localHostName)

  }
}
   // ("SPARK_LOCAL_HOSTNAME","migle")