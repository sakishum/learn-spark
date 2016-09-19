spark streaming


 KafkaUtils.createStream读kafka中数据中kafka-consumer-offset-checker.sh中的offset和lag不变!!


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



##HDFS
文件为什么读不出来

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