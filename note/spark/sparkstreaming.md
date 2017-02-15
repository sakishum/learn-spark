spark streaming


 KafkaUtils.createStream读kafka中数据中kafka-consumer-offset-checker.sh中的offset和lag不变!!

##transform 
转换RDD到另一个RDD

##foreachRDD 

```
        dstream.foreachRDD { rdd =>
          val where1 = "on the driver"
            rdd.foreach { record =>
              val where2 = "on different executors"
            }
          }
        }

```
//http://allegro.tech/2015/08/spark-kafka-integration.html



##Broadcast
累加器（Accumulators）和广播变量（Broadcast variables）是无法从Spark Streaming的检查点中恢复回来的



spark.streaming.kafka.maxRatePerPartition


##HDFS
文件为什么读不出来！！

```

def textFileStream(directory: String): DStream[String]
def fileStream[
    K: ClassTag,
    V: ClassTag,
    F <: NewInputFormat[K, V]: ClassTag
  ] (directory: String, filter: Path => Boolean, newFilesOnly: Boolean)

  fileStream还有两个兄弟
```
并且没有Path符合过滤条件并且文件修改时间在(modTimeIgnoreThreshold,currentTime] 并且没有被处理过的文件

>>所以文件内容有追加了文件也是不会处理

详细代码看
  private def findNewFiles(currentTime: Long): Array[String]
  private def isNewFile(path: Path, currentTime: Long, modTimeIgnoreThreshold: Long): Boolean
的具体实现

 val modTimeIgnoreThreshold = math.max(
        initialModTimeIgnoreThreshold,   // initial threshold based on newFilesOnly setting
        currentTime - durationToRemember.milliseconds  // trailing end of the remember window
      )
     
durationToRemember = slideDuration * math.ceil(minRememberDurationS.milliseconds.toDouble / batchDuration.milliseconds).toInt
minRememberDurationS = {
    Seconds(ssc.conf.getTimeAsSeconds("spark.streaming.fileStream.minRememberDuration",
      ssc.conf.get("spark.streaming.minRememberDuration", "60s")))
  }
  
initialModTimeIgnoreThreshold = if (newFilesOnly) clock.getTimeMillis() else 0L


实现不了根据不同文件名规则处理逻辑稍微有些不一样的情况，返回值里面没有带文件名！




需要关联外部数据来扩展或过滤时:
1. 数据量大、更新频繁：hbase,Redis直接每条查询
2. 数据量不大且没有变化:直接broadcast
3. 数据量不太大，有更新：重新广播  ？？？





>>追数据的时候可能在一个批次的时候间隔内处理多个批次的内容(处理速度变快了！，比如受hdfs，网络等因素的原因，前期处理速度过慢导致有数据积压，而之后问题解决，速度加快所以会。。)
