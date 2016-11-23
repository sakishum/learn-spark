##metastore
hive metastore 服务端启动命令：
hive --service metastore -p <port_num>
如果不加端口默认启动：hive --service metastore，则默认监听端口是：9083 
远程模式：hive服务和metastore在不同的进程内，可能是不同的机器,所有hive服务都连接到metastore服务，由metastore服务再连接到mysql。
hive-site.xml添加以下
<property>
<name>hive.metastore.uris</name>
<value>thrift://vm-centos-00:9083</value>
</property>
本地模式：hive服务和metastore在同一个进程内，metastore存储位置可以是本地或其它机器,相当于所有hive服务都直接连接到mysql获取metastore
所以hive-site.xml都需要配置连接到mysql的信息


内嵌模式：hive服务和metastore服务运行在同一个进程中，derby服务也运行在该进程中。
该模式无需特殊配置

- [ ] 完善！