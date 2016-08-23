/usr/local/spark/bin/spark-submit 
--packages org.apache.spark:spark-streaming-kafka_2.10:1.6.0 
--class "KafkaWordCount" --master local[4] target/scala-2.10/spark
-kafka-project_2.10-1.0.jar localhost:2181 <group name> <topic name> <number of threads>




bin/spark-submit --packages org.apache.spark:spark-streaming-kafka_2.10:1.6.0   --class "OutputToKafka"  --master spark://vm-centos-00:7077 /home/migle/spark-app/data-qcd-spark-app.jar
bin/spark-submit --packages org.apache.spark:spark-streaming-kafka_2.10:1.6.0   --class "OutputToKafka"  --master spark://vm-centos-00:7077 /home/migle/spark-app/data-qcd-spark-app.jar




bin/kafka-console-producer.sh --broker-list vm-centos-00:9092,vm-centos-01:9092   
bin/kafka-console-consumer.sh --zookeeper   vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181  --from-beginning --topic qcd_ruselt_netpay
bin/kafka-topics.sh --zookeeper   vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181  --delete   --topic qcd_ruselt_netpay
bin/kafka-topics.sh --zookeeper   vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181  --delete   --topic topic-1


bin/kafka-topics.sh  --zookeeper vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181  --list

kafka&spark streaming
http://blog.selfup.cn/1665.html
http://blog.cloudera.com/blog/2015/03/exactly-once-spark-streaming-from-apache-kafka/
http://allegro.tech/2015/08/spark-kafka-integration.html
http://spark.apache.org/docs/latest/submitting-applications.html
http://mkuthan.github.io/blog/2016/01/29/spark-kafka-integration2/



1:打包
    spark app 与规则解析分开打包
    
    http://www.jianshu.com/p/afb79650b606
    
2:连接池(合并redis,完成kafka)
3.多条件表达式

