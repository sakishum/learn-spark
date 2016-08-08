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
bin/kafka-server-start.sh config/server.properties

创建一个topic
bin/kafka-topics.sh --create --zookeeper vm-centos-01:2181 --replication-factor 1 --partitions 1 --topic kafkatest

查看topic 
bin/kafka-topics.sh --list --zookeeper vm-centos-01:2181
bin/kafka-topics.sh --describe --zookeeper vm-centos-01:2181 --topic kafkatest 

收、发消息
启动producer
bin/kafka-console-producer.sh --broker-list vm-centos-00:9092   --topic kafkatest

启动consumer
bin/kafka-console-consumer.sh --zookeeper   vm-centos-01:2181 --from-beginning --topic kafkatest

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




