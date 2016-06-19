             
spark.testing.memory(UnifiedMemoryManager.scala)
spark.testing.reservedMemory(UnifiedMemoryManager.scala)

driver.memory:也可以通过命令行参数*---driver-memory 2g* 指定,任务启动时被指定为jvm堆最大值*-Xmx*


spark.executor.memory:executor的内存大小,默认是512M,也可以通过命令行参数*--executor-memory  1g*指定
spark.storage.memoryFraction:默认是0.6。即默认每个executor的内存是512M，其中 512M*0.6=307.2M用于RDD缓存，  512M*0.4=204.8用于Task任务计算
>>如果executor报OOM内存不足，需要考虑增大spark.executor.memory。
如果频繁Full GC，可能是executor中用于Task任务计算的内存不足，就需要考虑降低spark.storage.memoryFraction的比例，即减小用于缓存的内存大小，增大用于Task任务计算的内存大小。
需要考虑优化RDD中的数据结构，减小数据占用的内存大小
>>spark-env.sh 中设置 JAVA_OPTS=" -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps"可以打印 GC 的相关信息。这样如果有GC发生，就可以在master和work的日志上看到。

spark 存储级别
MEMORY_ONLY   默认

节点物理内存
yarn内存
spark内存
三者之间的设置及关系




./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 300m \
    --executor-memory 200m \
    --executor-cores 1 \
   --num-executors 1 \
   --conf spark.testing=100 \    
    spark-examples*.jar \
    10 2>&1 | tee xxx.log
*--conf spark.testing=100*  虚拟机搭建的集群，内存很小，reservedMemory设为0

参考资料
[Spark 1.6 内存管理模型( Unified Memory Management)分析](http://www.jianshu.com/p/b250797b452a)