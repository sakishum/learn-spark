##TODO list
- [x] standlone环境搭建
- [x] 完成wordcount程序
- [ ] IDEA中调试Driver程序,跟踪分析Driver程序执行过程
- [x] RPC
- [ ] 序列化
- [ ] master分析
- [ ] worker分析
- [ ] Spark内存管理
- [ ] on yarn分析 
- [ ] RDD创建 
- [ ] Job划分stage
- [ ] Task分配算法

>>第一步大致流程，关键类
>>第二步较粗的实现细节
>>第三步骤具体实现细节

数据文件需要在每个节点上都有？
launcherBackend 和 launcherServer?s
broadcast


## 其它
### AKKA
- [ ] RemoteAKKA


<property>
<name>yarn.scheduler.minimum-allocation-mb</name>
<value>128</value>
</property>

<property>
    <!--结点物理内存-->
    <name>yarn.nodemanager.resource.memory-mb</name>
    <value>1024</value>
  </property>
  <property>
    <name>yarn.scheduler.minimum-allocation-mb</name>
    <value>128</value>
  </property>
  <property>
    <name>yarn.scheduler.maximum-allocation-mb</name>
    <value>256</value>
  </property>
  <property>
    <name>yarn.app.mapreduce.am.resource.mb</name>
    <value>128</value>
  </property>
<property>
<name>yarn.scheduler.minimum-allocation-mb</name>
<value>128</value>
</property>
<property>
<name>yarn.app.mapreduce.am.command-opts</name>
<value>-Xmx1024m</value>
</property>
<property>
<!--此值*物理内存=结点虚拟内存-->
<name>yarn.nodemanager.vmem-pmem-ratio</name>
<value>1.5</value>
</property>





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

    --driver-memory 为作业启动时-Xmx值



bin/spark-submit --class "StreamWordCount"  --master spark://vm-centos-00:7077  ./testjar/mspark-1.0-SNAPSHOT.jar

bin/spark-submit --class "StreamWordCount"  --master spark://vm-centos-00:7077  --executor-cores 1  --driver-memory 300m --executor-memory 200m  --num-executors 1  --conf spark.testing=100  ./testjar/mspark-1.0-SNAPSHOT.jar vm-centos-02 22222
     \
     \
    \
    \



bin/spark-submit --class "StreamWordCount"  --master local[4]  ./testjar/mspark-1.0-SNAPSHOT.jar localhost 9999

bin/spark-submit --class org.apache.spark.examples.streaming.NetworkWordCount --master spark://vm-centos-00:7077 spark-examples*.jar  localhost 9999 
 

./bin/spark-shell --master yarn  --deploy-mode client --executor-memory 100m  --driver-memory 200m --executor-cores 1
./spark-shell --master yarn  --deploy-mode cluster --executor-memory 1g --driver-memory 1g  --executor-cores 1

./spark-shell --master yarn  --deploy-mode client --executor-memory 1g --driver-memory 2g  --executor-cores 1
./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 500m \
    --executor-memory 200m \
    --executor-cores 1 \
   --num-executors 2 \
    spark-examples*.jar \
    10







    {{JAVA_HOME}}/bin/java -server -XX:OnOutOfMemoryError='kill %p' -Xms200m -Xmx200m -Djava.io.tmpdir={{PWD}}/tmp '-Dspark.driver.port=40805' '-Dspark.ui.port=0' -Dspark.yarn.app.container.log.dir=<LOG_DIR> org.apache.spark.executor.CoarseGrainedExecutorBackend --driver-url spark://CoarseGrainedScheduler@192.168.99.133:40805 --executor-id 5 --hostname vm-centos-02 --cores 1 --app-id application_1466265731428_0003 --user-class-path file:$PWD/__app__.jar 1> <LOG_DIR>/stdout 2> <LOG_DIR>/stderr

./bin/spark-submit --class org.apache.spark.examples.SparkPi \
    --master yarn \
    --deploy-mode cluster \
    --driver-memory 200m \
    --executor-memory 100m \
    --executor-cores 1 \
    spark-examples*.jar \
    10






spark-examples-1.5.1-hadoop2.6.0.jar

http://www.iteblog.com/archives/1223



yarn中*yarn.scheduler.maximum-allocation-mb* 的值小于spark executor 申请的内存时，spark作业初始化失败
