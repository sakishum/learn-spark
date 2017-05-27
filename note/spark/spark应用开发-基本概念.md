## RDD

RDD是Spark的核心，也是整个Spark的架构基础，可以总下出几个它的特性来：

它是不变的数据结构存储
它是支持跨集群的分布式数据结构
可以根据数据记录的key对结构进行分区
提供了粗粒度的操作，且这些操作都支持分区
它将数据存储在内存中，从而提供了低延迟性



http://litaotao.github.io/spark-questions-concepts
http://shiyanjun.cn/archives/744.html

## Job
## Stage
一个Job会被划分成一个或多个stage,job中有action或有会产shuffle的Transformations时会划分出新的stage

## Task 
一个stage会被划分成一个或多个task


