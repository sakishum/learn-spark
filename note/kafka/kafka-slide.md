---
theme : "blood" #"Sky"
transition: "slide"
highlightTheme: "darkula"
---

## kafka
  
---
## 使用场景

---
## 系统架构
- broker
- topic
- producer
- consumer
- partitions
- Consumer Group
--
### broker
Kafka集群包含一个或多个服务器，这种服务器被称为broker
--
### Topic 
每条发布到Kafka集群的消息都有一个类别，这个类别被称为Topic。（物理上不同Topic的消息分开存储，逻辑上一个Topic的消息虽然保存于一个或多个broker上但用户只需指定消息的Topic即可生产或消费数据而不必关心数据存于何处）
--

### Partition
Parition是物理上的概念，每个Topic包含一个或多个Partition. 

![](http://kafka.apache.org/20/images/log_anatomy.png)

--
### Producer
负责发布消息到Kafka broker
--
### Consumer
消息消费者，向Kafka broker读取消息的客户端。

--
### Consumer Group 

每个Consumer属于一个特定的Consumer Group（可为每个Consumer指定group name，若不指定group name则属于默认的group）。
![](http://kafka.apache.org/20/images/consumer-groups.png)  
---

## 常用命令
---
## 程序开发
---
## 优化