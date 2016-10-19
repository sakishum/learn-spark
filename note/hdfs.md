---
date:  2016-6-14 9:08:30

---



##
![image](http://hadoop.apache.org/docs/r2.6.4/hadoop-project-dist/hadoop-hdfs/images/hdfsarchitecture.png)
1. NameNode
         NameNode可以看作是分布式文件系统中的管理者，主要负责管理文件系统的命名空间、集群配置信息和存储块的复制等。NameNode会将文件系统的Meta-data存储在内存中，这些信息主要包括了文件信息、每一个文件对应的文件块的信息和每一个文件块在DataNode的信息等。
2. DataNode是文件存储的基本单元，它将Block存储在本地文件系统中，保存了Block的Meta-data，同时周期性地将所有存在的Block信息发送给NameNode。
3. Client就是需要获取分布式文件系统文件的应用程序。



2）文件写入
    Client向NameNode发起文件写入的请求。
    NameNode根据文件大小和文件块配置情况，返回给Client它所管理部分DataNode的信息。
    Client将文件划分为多个Block，根据DataNode的地址信息，按顺序写入到每一个DataNode块中。

3）文件读取
    Client向NameNode发起文件读取的请求。
    NameNode返回文件存储的DataNode的信息。
    Client读取文件信息。
参考：
1. [HDFS写文件过程分析](http://shiyanjun.cn/archives/942.html)
2. 
## 常用命令  
1. 文件操作`hdfs dfs`会列出，eg:
2. 文件系统：
hadoop dfsadmin -report  


hdfs fsck -blocks -files /XXXXx  #查看文件块信息

## 在线增加datanode 
1. 准备工作：ssh互信、hosts文件修改
2. 修改namenode结点slaves文件，添加新增结点(否则下次集群重启时不能自动拉起此结点)
3. 在新增结点起动datanode:`sbin/hadoop-daemon.sh start datanode`
4. hdfs dfsadmin -refreshNodes

## 在线删除datanode 
不能直接停止，需要先转移数据再停止datanode
1. 修改配置dfs.hosts和dfs.hosts.exclude执行`hadoop dfsadmin -refreshNodes`
2. 停止结点，再将节点地址去掉再执行`hadoop dfsadmin -refreshNodes`
 
## 利用SecondaryNameNode恢复NameNode



## api




## TODO
- [] 分析读写文件部分源代码





http://my.oschina.net/crxy/blog/348868
http://www.weixuehao.com/archives/category/bigdata/hadoop
http://www.codeceo.com/article/hdfs-block-list.html