### KafkaUtils.createDirectStream or KafkaUtils.createStream
1. *createDirectStream*:Direct DStream方式由kafka的SimpleAPI实现 ，比较灵活，可以自行指定起始的offset，性能较createStream高，
SparkStreaming读取时在其内自行维护offset但不会自动提交到zk中,如果要监控offset情况，需要自己实现。
>>spark-streaming-kafka-0-10中已经实现offset自动提交zk中

2. *createStream*:采用了Receiver DStream方式由kafka的high-level API实现

最新的实现中createDirectStream也可以提交offset了spark-streaming-kafka-0-10<http://spark.apache.org/docs/latest/streaming-kafka-integration.html>但要求 kafka是0.10.0及以后。

### createDirectStream中的offset
createDirectStream不会自动提交offset到zk中，不能很方便的监控数据消费情况
 
 ```
 KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(topic))
        .transform(rdd => {
        val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        for (offset <- offsets) {
            val topicAndPartition = TopicAndPartition(offset.topic, offset.partition)
            //保存offset至zk可redis中方便监控
            //commitOffset(kafkaParams,groupId, Map(topicAndPartition -> offset.untilOffset))
        }
        rdd
        })

```

如果可以只是用来监控消费情况在transform中转换成HasOffsetRanges取出offset保存到zk中即可，
>>"rdd.asInstanceOf[HasOffsetRanges].offsetRanges" 如果已经经过其它Transformations或output操作之后此rdd已经不是KafkaRDD时再转换会报错！！

另外还有一个控制能更强的createDirectStream方法，可以指定fromOffsets和messageHandler
def createDirectStream(
      ssc: StreamingContext,
      kafkaParams: Map[String, String],
      fromOffsets: Map[TopicAndPartition, Long],
      messageHandler: MessageAndMetadata[K, V] => R
  )
>>可以将offset保存在zk或redis等外部存储中方便监控，然后下次启动时再从中读取

### 分区partition
Kafka中的partition和Spark中的partition是不同的概念，但createDirectStream方式时topic的总partition数量和Spark和partition数量相等。
        ```
        //KafkaRDD.getPartitions
         override def getPartitions: Array[Partition] = {
            offsetRanges.zipWithIndex.map { case (o, i) =>
                val (host, port) = leaders(TopicAndPartition(o.topic, o.partition))
                new KafkaRDDPartition(i, o.topic, o.partition, o.fromOffset, o.untilOffset, host, port)
            }.toArray
          }
        
        ```
partition中数据分布不均会导致有些任务快有些任务慢，影响整体性能，可以看情况做*repartition*，单个topic比较容易实现partition中数据分布均匀，但如果同一个程序中需要同时处理多个topic的话，可以考虑能否合并成一个topic，增加partition数量，不过topic很多时间会和其它系统共用，所以可能不容易合并，这情况只能做repartition。虽然repartition会消耗一些时间，但总的来说，如果数据分布不是很均匀的话repartition还是值得，repartition之后各任务处理数据量基本一样，而且Locality_level会变成“PROCESS_LOCAL”

>>！！使用flume加载到kafka的使用默认配置十有八九分布不匀

### 检查点
代码：

```
Object SparkApp(){
def gnStreamContext(chkdir:String,batchDuration: Duration,partitions:Int)={
    val conf = new SparkConf().setAppName("GnDataToHive") //.setMaster("local[2]")
    val ssc = new StreamingContext(conf, batchDuration)
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, Set(topic))
    ...........
    ...........
    ...........
    val terminfos = ssc.sparkContext.broadcast(ttis) //TODO:可不可以启一个线程周期性广播？？？？
    ssc.checkpoint(chkdir)
    ssc
  }
 def main(args: Array[String]): Unit = {
    val chkdir="hdfs://hacluster/yx_qcd/chkpoint/gndatatohive-chkpoint"
    val chkssc = StreamingContext.getOrCreate(chkdir,()=>gnStreamContext(chkdir,Seconds(args(0).toInt),args(1).toInt))
    chkssc.start()
    chkssc.awaitTermination()
  }
}
```
offset会在保存至检查点中，下次启动会继续接着读取但是以下问题需要注意：

1. kafka中数通常保存周期都不会太长，都有清理周期，如果记录的offset对应数据已经被清理，从检查点恢复时程序会一直报错。 //TODO 处理

2. 如果程序逻辑发生变化，需要先删除检查点，否则不管数据还是逻辑都会从旧检查点恢复。 

### 限流

可以用*spark.streaming.kafka.maxRatePerPartition*指定每个批次从每个partition中每秒中最大拉取的数据量，比如将值设为1000的话，每秒钟最多从每个partition中拉取1000条数据，如果batchDuration设为1分钟的话，则每个批次最多从每个partition中拉取60000条数据。
此值要设置合理，太小有可能导致资源浪费，但kafka中的数据消费不完，太多又达不到限流的目的

具体代码见:
DirectKafkaInputDStream.maxMessagesPerPartition    
DirectKafkaInputDStream.clamp

        ```
         // limits the maximum number of messages per partition
          protected def clamp(
            leaderOffsets: Map[TopicAndPartition, LeaderOffset]): Map[TopicAndPartition, LeaderOffset] = {
            maxMessagesPerPartition.map { mmp =>
              leaderOffsets.map { case (tp, lo) =>
                tp -> lo.copy(offset = Math.min(currentOffsets(tp) + mmp, lo.offset))
              }
            }.getOrElse(leaderOffsets)
          }
        ```

spark-submit提交时带上即可：`--conf spark.streaming.kafka.maxRatePerPartition=10000`   
>> 貌似只能在createDirectStream中起作用，在createStream方式中没看到有类似设置

### hdfs输出文件名：
写入hdfs时默认目录名格式为："prefix-TIME_IN_MS.suffix"，每个目录下的文件名为"part-xxxx"。
如果只想自定义目录名可以通过foreachRDD，调用RDD的saveAsXXX `dstream.foreachRDD(rdd=>rdd.saveAsxxxx(""))`  
如果需要自定义输出的文件名，需要自定义一个FileOutputFormat的子类，修改getRecordWriter方法中的name即可，然后调用`saveAsHadoopFile[MyTextOutputFormat[NullWritable, Text]]`。

### 外部数据关联
某些情况下载关联外部数据进行关联或计算。
1. 外部数据放在redis中,在`mapPartitions`中关联
2. 外部数据放在hdfs中，启动是broadcast发送后再关联


### 其它
1. 日志：提交作业时spark-submit默认会读取$SPARK_HOME/conf/log4j.properties如果需要自定义可以在提交作业时可以带上 --conf spark.driver.extraJavaOptions=-Dlog4j.configuration=file://xx/xx/log4j.properties

