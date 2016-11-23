import java.time.{LocalDate, LocalTime}

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2
import org.apache.hadoop.hbase.protobuf.generated.CellProtos.KeyValue
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by migle on 2016/10/31.
  */
object OutputHFile {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("OutputHFile").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val rdd = sc.parallelize(1 to 100).map(x=>
      ((Bytes.toBytes(x),(Bytes.toBytes("F"),Bytes.toBytes("hex"),Bytes.toBytes(Integer.toHexString(x))))))

    val hbconf = HBaseConfiguration.create()
    val hdata = rdd.map(x=>(new ImmutableBytesWritable(x._1),new KeyValue(x._2._1,x._2._2,x._2._3)))


    hdata.saveAsNewAPIHadoopFile("hdfs://hacluster/yx_qcd/gnstream/hfiles/" + LocalDate.now().toString + "-" + LocalTime.now().toString.replaceAll(":","-"),
      classOf[ImmutableBytesWritable],
      classOf[KeyValue],
      classOf[HFileOutputFormat2],
      hbconf)
  }
}
