### KafkaUtils.createDirectStream or KafkaUtils.createStream
1. *createDirectStream*:Direct DStream方式由kafka的SimpleAPI实现 ，比较灵活，可以自行指定起始的offset，性能较createStream高，
SparkStreaming读取时在其内自行维护offset但不会自动提交到zk中,如果要监控offset情况，需要自己实现。
>>spark-streaming-kafka-0-10中已经实现offset自动提交zk中

2. *createStream*:采用了Receiver DStream方式由kafka的high-level API实现

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
>>"rdd.asInstanceOf[HasOffsetRanges].offsetRanges" 如果已经经过其它Transformations或output操作之后此rdd已经不是KafkaRDD时再转换会报错！

另外还有一个控制能更强的createDirectStream方法，可以指定fromOffsets和messageHandler
def createDirectStream(
      ssc: StreamingContext,
      kafkaParams: Map[String, String],
      fromOffsets: Map[TopicAndPartition, Long],
      messageHandler: MessageAndMetadata[K, V] => R
  )
>>可以将offset保存在zk或redis方便监控，然后下次启动时再从中读取

### 分区partition
Kafka中的partition和Spark中的partition是不同的概念，但createDirectStream方式时topic的总partition数量和Spark和partition数量。
        ```
        //KafkaRDD.getPartitions
         override def getPartitions: Array[Partition] = {
            offsetRanges.zipWithIndex.map { case (o, i) =>
                val (host, port) = leaders(TopicAndPartition(o.topic, o.partition))
                new KafkaRDDPartition(i, o.topic, o.partition, o.fromOffset, o.untilOffset, host, port)
            }.toArray
          }
        
        ```
partition中数据分布不均会导致有些任务，有些任务慢，影响整体性能，可以看情况做*repartition*，单个topic比较容易实现partition中数据分布均匀，但如果同一个程序中同时处理多个topic的话，可以考虑合并成一个topic，增加partition数量，不过topic很多时间会和其它系统共用，所以可能不容易合并，这情况只能做repartition。虽然repartition会消耗一些时间，但总的来说，如果数据分布不是很均匀的话repartition还是值得，repartition之后各任务处理数据量基本一样，而且Locality_level会变成“PROCESS_LOCAL”

>>！！使用flume加载到kafka的使用默认配置十有八九分布不匀

### 检查点


### 限流
正常情况下SparkStreaming的资源配置一般是满足正常情况下kafka中的数据处理，但是在某些特殊情况下，如果kafka中的数据暴涨的话，会导致每批次处理数据太多，单个处理时间变长甚至崩溃，所以就需要限制每个批次处理的最大数据量。
*spark.streaming.kafka.maxRatePerPartition*用来指定每个批次每个partition中每秒中最大拉取的数据量比如将值设为1000的话，每秒钟最多从每个partition中拉取1000条数据，如果batchDuration设为1分钟的话，则每个批次最多从每个partition中拉取60000条数据。

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

### 外部数据关联

### 其它
1. 日志
2. hdfs输出文件名

