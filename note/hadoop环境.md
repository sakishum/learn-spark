## 规划
在vmware中添加4台虚拟机.主机名，ip如下：
1. vm-centos-00   192.168.254.130    
2. vm-centos-01   192.168.254.131  
3. vm-centos-02   192.168.254.132  
4. vm-centos-03   192.168.254.133  
>>可以先装好一台之后克隆3份，然后再分别修改主机名和ip地址

vm-centos-00: mysql、动态扩容实验   
vm-centos-01：namenode   
vm-centos-02\vm-centos-03: datanode    

## 基础环境
1. 固定ip设置
    编辑：*/etc/sysconfig/network-scripts/ifcfg-e....*
```
        TYPE=Ethernet
        DEFROUTE=yes
        PEERDNS=yes
        PEERROUTES=yes
        IPV4_FAILURE_FATAL=no
        IPV6INIT=yes
        IPV6_AUTOCONF=yes
        IPV6_DEFROUTE=yes
        IPV6_PEERDNS=yes
        IPV6_PEERROUTES=yes
        IPV6_FAILURE_FATAL=no
        NAME=eno16777736
        DEVICE=eno16777736
        ONBOOT=yes
        BOOTPROTO=static
        IPADDR=192.168.99.130
        GATEWAY=192.168.99.1
        NETMASK=255.255.255.0
```
2. 主机名
   >>注意:主机名最好能好写好记，不要包含下划线“_”
    依次：
        `hostnamectl set-hostname  vm-centos-00`    //130
        `hostnamectl set-hostname  vm-centos-01`    //131
        `hostnamectl set-hostname  vm-centos-02`    //132
        `hostnamectl set-hostname  vm-centos-03`    //133

3. ssh互信配置
  a.依次生成密钥： `ssh-keygen -t rsa`  
  b.将其它三台公钥复制到130,在130合并至*authorized_keys* 后依次分发到其它机器:

    ```
        scp migle@192.168.254.131:/home/migle/.ssh/id_rsa.pub ./a.pub
        scp migle@192.168.254.132:/home/migle/.ssh/id_rsa.pub ./b.pub
        scp migle@192.168.254.133:/home/migle/.ssh/id_rsa.pub ./c.pub
  
    cat id_rsa.pub >> authorized_keys   
    cat ./a.pub >> authorized_keys
    cat ./b.pub >> authorized_keys
    cat ./c.pub >> authorized_keys

    scp ./authorized_keys  migle@192.168.254.133:/home/migle/.ssh/authorized_keys 
  ```
4. hosts文件

    192.168.254.130  vm-centos-00
    192.168.254.131  vm-centos-01
    192.168.254.132  vm-centos-02
    192.168.254.133  vm-centos-03

scp至其它机器

4. jdk安装
    略

##安装hadoop

    gzip -d hadoop-2.7.3.tar.gz
    tar xvf hadoop-2.7.3.tar

### core-site.xml

    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://vm-centos-01:9999</value>
    </property>

###hdfs-site.xml

    <!--namenode-->
    <property>
            <name>dfs.namenode.name.dir</name>
            <value>/opt/hadoop/namedir</value>
    </property>
    <!--DataNode-->
    <property>
            <name>dfs.datanode.data.dir</name>
            <value>/opt/hadoop/datadir</value>
    </property>

###yarn-site.xml


    <!-- Site specific YARN configuration properties -->
    <property>
            <name>yarn.resourcemanager.address</name>
            <value>vm-centos-01:8032</value>
    </property>
    <property>
            <name>yarn.resourcemanager.scheduler.address</name>
            <value>vm-centos-01:8030</value>
    </property>
    
    <property>
            <name>yarn.resourcemanager.resource-tracker.address</name>
            <value>vm-centos-01:8031</value>
    </property>
    
    <property>
            <name>yarn.resourcemanager.admin.address</name>
            <value>vm-centos-01:8033</value>
    </property>
    
    <property>
            <name>yarn.resourcemanager.webapp.address</name>
            <value>vm-centos-01:8088</value>
    </property>

分发到其它结点:
  `scp -r /opt/hadoop-2.7.3 vm-centos-02:/opt/`




创建数据目录
    mkdir -p /opt/hadoop/datadir ##在datanode执行
    mkdir -p /opt/hadoop/namedir ##在namenode执行 

格式化新的文件系统
    $HADOOP_HOME/bin/hdfs namenode -format mhadoop

### 启动

`$HADOOP_HOME/sbin/start-dfs.sh`
`$HADOOP_HOME/sbin/start-yarn.sh`

##测试
上传文件：hdfs dfs -put dw_user_info.dat /hdata

//TODO 
动态增加节点
动态增加节点后已有文件是否会发生变化 ？

namenode挂的情况

动态增加yarn的nodemanager
resourcemanager ha

hdfs web ui:http://192.168.99.131:50070/
yarn web ui:http://192.168.99.131:8088/cluster

wordcount
hadoop jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.3.jar wordcount /hdata/getdata.py /hdata/test

## hadoop native

### 安装依赖
>>每个结点都需要安装

        sudo yum -y install lzo-devel zlib-devel autoconf automake  libtool cmake openssl-devel 
        sudo yum install snappy snappy-devel
        sudo yum install bzip2 bzip2-devel

#### 安装protobuf

        wget https://github.com/google/protobuf/releases/download/v2.5.0/protobuf-2.5.0.tar.bz2
        ./configure
        make   
        make install 

### 编译hadoop
    
        mvn clean package -DskipTests -Pdist,native -Dtar  -Dbundle.snappy  -Dsnappy.lib=/usr/lib64

### 复制native lib 至hadoop 

        cd /opt/hadoop-2.7.3-src/hadoop-dist/target/hadoop-2.7.3/lib/native
        cp * $HADOOP_HOME/lib/native
        hadoop checknatve  ## 查看结果
之前：
➜  ~ hadoop checknative
16/10/13 15:09:13 WARN bzip2.Bzip2Factory: Failed to load/initialize native-bzip2 library system-native, will use pure-Java version
16/10/13 15:09:13 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
Native library checking:
hadoop:  true /opt/hadoop-2.7.3/lib/native/libhadoop.so
zlib:    true /lib64/libz.so.1
snappy:  false 
lz4:     true revision:99
bzip2:   false 
openssl: false Cannot load libcrypto.so (libcrypto.so: 无法打开共享对象文件: 没有那个文件或目录)!

之后：

➜  native hadoop checknative                             
16/10/13 16:00:16 INFO bzip2.Bzip2Factory: Successfully loaded & initialized native-bzip2 library system-native
16/10/13 16:00:16 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
Native library checking:
hadoop:  true /opt/hadoop-2.7.3/lib/native/libhadoop.so.1.0.0
zlib:    true /lib64/libz.so.1
snappy:  true /opt/hadoop-2.7.3/lib/native/libsnappy.so.1
lz4:     true revision:99
bzip2:   true /lib64/libbz2.so.1
openssl: true /lib64/libcrypto.so


//TODO

core-site.xml中添加
<property>
    <name>io.compression.codecs</name>
    <value>
      org.apache.hadoop.io.compress.GzipCodec,
      org.apache.hadoop.io.compress.DefaultCodec,
      org.apache.hadoop.io.compress.BZip2Codec,
      org.apache.hadoop.io.compress.SnappyCodec
    </value>
</property>


LD_LIBRARY_PATH=$HADOOP_HOME/lib/native  hbase --config $HBASE_HOME/conf org.apache.hadoop.util.NativeLibraryChecker 

hbase org.apache.hadoop.hbase.util.CompressionTest hdfs://vm-centos-01:9999/user/migle/output-1474182810000/part-00003 snappy

在应用程序中使用
Configuration conf = new Configuration();
 
//对map输出的内容进行压缩
conf.set("mapred.compress.map.output","true");
conf.set("mapred.map.output.compression.codec",
                      "org.apache.hadoop.io.compress.SnappyCodec");
 
//对reduce输出的内容进行压缩
conf.set("mapred.output.compress","true");
conf.set("mapred.output.compression",
                     "org.apache.hadoop.io.compress.SnappyCodec");




#Hive

bin/hadoop fs -mkdir       /tmp
bin/hadoop fs -mkdir   -p    /user/hive/warehouse
bin/hadoop fs -chmod g+w   /tmp
bin/hadoop fs -chmod g+w   /user/hive/warehouse

修改hive-env.sh
HADOOP_HOME=**/opt/hadoop-2.7.3**
安装mariadb-server 
vm-centos-00上
yum install mariadb-server

systemctl start mariadb // 启动mariadb
systemctl enable mariadb //设置成开机自启动
mysql_secure_installation ///设置 root密码等相关,根据提示操作   root/iammigle

//允许root远程方便管理
mysql -uroot -p
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'iammigle' WITH GRANT OPTION;
flush privileges;

新建数据hive,新建用户hive/hive

GRANT ALL ON `hive`.* TO 'hive'@'%';

GRANT GRANT OPTION ON `hive`.* TO 'hive'@'%';
复制mysql jdbc driver 到hive/lib目录下

hive-site.xml
  <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://vm-centos-00:3306/hive?createDatabaseIfNotExist=true</value>
      <description>JDBC connect string for a JDBC metastore</description>
      </property>

      <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
          <value>com.mysql.jdbc.Driver</value>
            <description>Driver class name for a JDBC metastore</description>
            </property>

            <property>
              <name>javax.jdo.option.ConnectionUserName</name>
                <value>hive</value>
                  <description>username to use against metastore database</description>
                  </property>

                  <property>
                    <name>javax.jdo.option.ConnectionPassword</name>
                      <value>hive</value>
                        <description>password to use against metastore database</description>
                        </property>

初始化schema
./schematool -initSchema -dbType mysql


启动
./bin/hive
在hive中创建一个表
CREATE TABLE pokes (foo INT, bar STRING);



#HBASE


## 其它
1. 查看时区 timedatectl 
2. 设计时区  
>>其实不考虑各个发行版的差异化, 从更底层出发的话, 修改时间时区比想象中要简单:
cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

##TODO
学习使用 fabric