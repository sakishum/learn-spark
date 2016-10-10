import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{Text, LongWritable}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created by migle on 2016/9/18.
 */
object HDFSStreamingWithChk {

  //有了checkpoint后，程序如果遇到异常而退出，在此期间写入此目录的文件，程序下次启动后能继续接着处理
  def createContext(checkpointdir: String,datafile:String)={
    val conf = new SparkConf().setAppName("StreamingWithHDFS").setMaster("local[4]")
    //conf.set("spark.streaming.fileStream.minRememberDuration", "360000s")
    val ssc = new StreamingContext(conf, Seconds(60))
    val dstream = ssc.fileStream[LongWritable, Text, TextInputFormat](datafile,(path:Path)=>{!path.getName().startsWith(".")}, newFilesOnly=false)
    dstream.map(m => (m._1.toString, m._2.toString)).print()
    ssc.checkpoint(checkpointdir)
    ssc
  }

  def main(args: Array[String]) {
    val checkpointdir = "hdfs://vm-centos-01:9999/user/migle/chkpoint"
    val datafile = "hdfs://vm-centos-01:9999/user/migle/data"
    val chkstream = StreamingContext.getOrCreate(checkpointdir,  () =>{createContext(checkpointdir,datafile)})
    chkstream.start()
    chkstream.awaitTermination()
  }
}
