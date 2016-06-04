##
![image](http://hadoop.apache.org/docs/r2.6.4/hadoop-project-dist/hadoop-hdfs/images/hdfsarchitecture.png)


## 常用命令  
1. 文件操作`hdfs dfs`会列出，eg:
2. 文件系统：
hadoop dfsadmin -report  

## 在线增加datanode 

1. 准备工作：ssh互信、hosts文件修改
2. 修改namenode结点slaves文件，添加新增结点(否则下次集群重启时不能自动拉起此结点)
3. 在新增结点起动datanode:`sbin/hadoop-daemon.sh start datanode`
4. hdfs dfsadmin -refreshNodes

## 在线删除datanode 

## namenode恢复  



## api



http://my.oschina.net/crxy/blog/348868
http://www.weixuehao.com/archives/category/bigdata/hadoop
http://www.codeceo.com/article/hdfs-block-list.html