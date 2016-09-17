##kafka
Kafka同时支持点到点分发模型（Point-to-point delivery model），多个消费者共同消费队列中某个消息的单个副本，
以及发布-订阅模型（Publish-subscribe model），即多个消费者接收自己的消息副本

http://www.infoq.com/cn/articles/apache-kafka/
http://blog.jobbole.com/75328/

##安装配置
vim config/server.properties

修改：
broker.id=1
zookeeper.connect=vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181

启动：
>>先启动zk集群
启动：bin/kafka-server-start.sh config/server.properties

创建一个topic
bin/kafka-topics.sh --create --zookeeper vm-centos-01:2181 --replication-factor 1 --partitions 3 --topic kafkatest

查看topic 
bin/kafka-topics.sh --list --zookeeper vm-centos-01:2181
bin/kafka-topics.sh --describe --zookeeper vm-centos-01:2181 --topic kafkatest 

收、发消息
启动producer
bin/kafka-console-producer.sh --broker-list vm-centos-00:9092   --topic kafkatest

启动consumer
bin/kafka-console-consumer.sh --zookeeper   vm-centos-01:2181 --from-beginning --topic kafkatest




bin/kafka-topics.sh --zookeeper vm-centos-01:2181 --describe  --topic kafkatest 
查看分区\leader\

查看Consumer的Group、Topic、分区ID、分区对应已经消费的Offset、logSize大小，Lag以及Owner等信息
bin/kafka-consumer-offset-checker.sh --zookeeper www.iteblog.com:2181 --topic test --group spark --broker-info





更多管理工具
http://blog.csdn.net/wuliusir/article/details/51062904

## 集群
scp -r /opt/kafka_2.11-0.9.0.1 vm-centos-01:/opt/
vim config/server.properties
修改：
broker.id=2

启动



##体系结构
kafka的消息分几个层次：
1. Topic：一类消息，例如page view日志，click日志等都可以以topic的形式存在，kafka集群能够同时负责多个topic的分发
2. Partition： Topic物理上的分组，一个topic可以分为多个partition，每个partition是一个有序的队列。partition中的每条消息都会被分配一个有序的id（offset）。
3. Message：消息，最小订阅单元

同一个topic的同一个Partition只能被同一个group的一个Consumer消费，也就是说如果Consumer数多于Partition数时会有Consumer空闲


##JAVA
**注意server与Consumer、Producer版本的兼容！！！**


http://www.infoq.com/cn/articles/apache-kafka/




python:
git clone https://github.com/mumrah/kafka-python
cd kafka-python
sudo python setup.py install

from kafka import KafkaProducer
kafka = KafkaProducer(bootstrap_servers='vm-centos-01:9092')
kafka.send('kafkatest', b"helo kafka-python")



########其它
Kafka在Zookeeper中动态维护了一个ISR（in-sync replicas） set，这个set里的所有replica都跟上了leader，只有ISR里的成员才有被选为leader的可能

##关于 offset
auto.commit.enable默认为true, consumer会定时地将offset写入到zookeeper上(时间间隔由auto.commit.interval.ms决定，默认1分钟).
收到消息但还没有commit offset时如果程序结束下次启动时会重复收到消息 //TODO:code

##关于auto.offset.reset
smallest, 将offset设为当前所能用的最小的offset。 注意不一定是0。
largest, 将offset设为当前可用的最大的offset。也就是consumer将只处理最新写入的消息。 默认值。


###
kafka.utils.ZkUtils
类中定义了从zk查询kafka的broker\topic\consumer等的信息方法


从指定位置开始读取
1. 修改zk中的offset
`set  /consumers/[groupid]/offsets/[topic]/[partitionid] offset`
set  /consumers/g1/offsets/sdi_scdt_3/0 10



auto.offset.reset:*如果zookeeper中没有初始化的offset或者被清空时*从那个offset开始消费的问题

smallest：如果zookeeper中没有初始化的offset时，从最小位置开始
largest：如果zookeeper中没有初始化的offset时，从最大位置开始，也就是新生产的消息才能被消费到

如何zk中有offset,则程序开始后从接着消费后面的消息


TODO:
1.如何只读取指定条数的信息  
2. offset何时提交？


关于offset 
各种样例代码中的getLeaderOffsets其实取的是可以读取(topic,partition)的最大或最小的offset
而不是此consumer上次消费到的offset,要读取这个offset需要读取zk，当然前提是已提交到zk
