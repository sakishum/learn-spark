import java.net.InetAddress

import me.migle.redis.ReidsPool
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
/**
 * Created by migle on 2016/8/19.
 * 测试redis连接池
 * bin/spark-submit  --packages redis.clients:jedis:2.8.1   --class "StreamingWithRedis"  --master spark://vm-centos-00:7077 /home/migle/spark-app/mspark.jar
 */
object StreamingWithRedis {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("StreamingWithRedis")
    val ssc = new StreamingContext(conf, Seconds(10));
    ////这里面可以broadcast不？
    sys.addShutdownHook()
    val dstream = ssc.socketTextStream("vm-centos-02", 9999);
    //lazy val jedisPool = new JedisPool(new GenericObjectPoolConfig, "192.168.99.130", 6379, 2000, "redispass")
    println("========================================")
    dstream.map(x=>x+"|").foreachRDD(
      //这里面可以broadcase不？
      rdd => {
        println("--------------------")
        println("+" * 10)
        println(InetAddress.getLocalHost.getHostName)
        println("+" * 10)
        //val jdriver = RedisClient.pool.getResource
        val jdriver = ReidsPool.pool.getResource
        jdriver.lpush("test_spark", "driver:" + InetAddress.getLocalHost.getHostName);
        jdriver.close();
        rdd.foreach(line => {
          //val jedis = RedisClient.pool.getResource
          val jedis = ReidsPool.pool.getResource
          jedis.lpush("test_spark", line + ":" + InetAddress.getLocalHost.getHostName);
          println("+" * 10)
          println(InetAddress.getLocalHost.getHostName)
          println("+" * 10)
          jedis.close()
        })
      })
    ssc.start()
    ssc.awaitTermination()
  }
}

//object RedisClient extends Serializable{
//  lazy val pool = new JedisPool(new GenericObjectPoolConfig, "192.168.99.130", 6379, 2000, "redispass")
//  val hook = new Thread{
//    override def run(): Unit = {
//      pool.destroy();
//    }
//  }
//  sys.addShutdownHook(hook.run())
//}
