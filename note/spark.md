#standlone
1. 创建slaves文件，添加worker节点
2. 复制spark到其它节点
3. ./sbin/start-all.sh  启动master及worker
4. 启动成功后可以在http://vm-centos-00:8080/查看集群信息

- [x]  spark-sql on hive
复制hive-site.xml到spark的conf目录下
spark-env.sh中添加`export SPARK_CLASSPATH=$HIVE_HOME/lib/mysql-connector-java-5.1.38-bin.jar:$SPARK_CLASSPATH`
./spark-shell运行

show tables;
select acc_nbr ,sum(g4_flow) from dw_gprs_flow group by acc_nbr limit 10;
.......
>>日志特别多，可以修改log4j.properites文件中的日志级别
- [ ] spark-sql on hive 运行在 yarn模式下
- [ ] spark-shell 运行在yarn模式下

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



 bin/spark-submit \
 --class "SparkPi" \
 --master spark://vm-centos-00:7077 \
testjar/mspark-1.0-SNAPSHOT.jar












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




## 其它
1. IDEA中调试Driver程序
IDEA中调度程序时需要加入
sc.addJar("xxx.jar")
可以在IDEA中设置一下，运行前package一下
另外：maven中scala代码是不会被compile和package的，**需要加插件**