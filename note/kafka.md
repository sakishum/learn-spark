
vim config/server.properties

修改：
broker.id=1
zookeeper.connect=vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181

启动：
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




