## zookeeper
### 安装配置
*   wget http://mirrors.koehn.com/apache/zookeeper/zookeeper-3.4.8/zookeeper-3.4.8.tar.gz   
*   tar zxvf zookeeper-3.4.8.tar.gz    
*   cd zookeeper-3.4.8/conf
*　 cp zoo_sample.cfg zoo.cfg  
*   cd ../ && bin/zkServer.sh start　　启动服务器
*   bin/zkCli.sh -server 127.0.0.1:2181　测试, 另外可以在这里做些测试，比如查看，创建znode等等

### 集群安装 
zoo.cfg  添加以下
server.0=vm-centos-00:2888:3888
server.1=vm-centos-01:2888:3888
server.2=vm-centos-02:2888:3888
server.3=vm-centos-03:2888:3888

每个服务器的dataDir目录下创建一个名为myid文件，内容就是server.X中的X
echo 3 >> /opt/hadoop/zkdata/myid

依次启动
./zkServer.sh start

./zkServer.sh status
ZooKeeper JMX enabled by default
Using config: /opt/zookeeper-3.4.8/bin/../conf/zoo.cfg
Mode: leader

可以kill掉为leader，然后看观察一下其它结点的情况

```
[zk: 127.0.0.1:2181(CONNECTED) 2] ls /
[zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 3] create /ztest migle'sdata 
Created /ztest
[zk: 127.0.0.1:2181(CONNECTED) 4] ls /
[ztest, zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 5] get /ztest
migle'sdata
cZxid = 0x2
ctime = Wed Apr 16 17:55:07 CST 2014
mZxid = 0x2
mtime = Wed Apr 16 17:55:07 CST 2014
pZxid = 0x2
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 11
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 6] set  /ztest xxxxxx
cZxid = 0x2
ctime = Wed Apr 16 17:55:07 CST 2014
mZxid = 0x3
mtime = Wed Apr 16 17:57:04 CST 2014
pZxid = 0x2
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 7] get /ztest        
xxxxxx
cZxid = 0x2
ctime = Wed Apr 16 17:55:07 CST 2014
mZxid = 0x3
mtime = Wed Apr 16 17:57:04 CST 2014
pZxid = 0x2
cversion = 0
dataVersion = 1
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 6
numChildren = 0
[zk: 127.0.0.1:2181(CONNECTED) 8] delete /ztest
[zk: 127.0.0.1:2181(CONNECTED) 9] ls /
[zookeeper]
[zk: 127.0.0.1:2181(CONNECTED) 10] 　
```

### 开发
#### 配置中心
#### 分布式锁
#### 分布式队列

###参考
* 阿里中间件团队博客: <http://jm-blog.aliapp.com/?tag=zookeeper>
* 分布式服务框架 Zookeeper -- 管理分布式环境中的数据 <http://www.ibm.com/developerworks/cn/opensource/os-cn-zookeeper/>  



　