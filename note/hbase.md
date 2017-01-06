## 安装配置
* 条件：Hadoop集群已搭建
* 下载  .....................
* 安装：` tar zxvf hbase-0.98.1-hadoop2-bin.tar.gz `  
* 集群模式配置：  
    单机模式和伪分布式请参见官方文档，集群模式请先保证你已经配置好Hadoop集群
    在*conf/hbase-site.xml*中添加以下：

hbase-env.sh中设置JAVA_HOME

``` 
<configuration>
<property>
<name>hbase.rootdir</name>
<value>hdfs://vm-centos-01:9999/hbase</value>
</property>
<property>  
<name>hbase.zookeeper.quorum</name>  
<value>vm-centos-01</value>  
<!--<value>vm-centos-01,vm-centos-02,vm-centos-03</value> -->
</property>
<property>  
<name>hbase.zookeeper.property.dataDir</name>  
<value>/opt/hadoop/zkdata</value>  
</property> 
<property> 
    <property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
        <description>The mode the cluster will be in. Possible values are
            false: standalone and pseudo-distributed setups with managed Zookeeper
            true: fully-distributed with unmanaged Zookeeper Quorum (see hbase-env.sh)
        </description>
    </property>
    <property>
        <name>hbase.master.port</name>
        <value>60000</value>
    </property>
</configuration>

```
在*conf/regionservers*中添加:  

``` 
vm-centos-02
vm-centos-03
```

>>vm-centos-01是Hadoop的Master，NameNode也在上面    

复制到其它结点：  

`scp -r hbase-0.98.1-hadoop2  135.191.27.161:/opt/hadoop/`  
`scp -r hbase-0.98.1-hadoop2  135.191.27.176:/opt/hadoop/`
`scp -r hbase-0.98.1-hadoop2  135.191.27.156:/opt/hadoop/`    

启动HBASE  
`./bin/start-hbase.sh`   

启动HBASE SHELL
`./bin/hbase shell`  


**文件打开限制修改**
**HDFS配置修改**

jps
175上会有：HMaster进程
其它机器上会有HRegionServer和HQuorumPeer进程


HMaster Web界面,*版本升级了端口变了16010*
http://vm-centos-01:16010/

## 测试

从VGOP的库上导一个用户基本信息表出来
db2 "export to gdi_mbuser_baseinfo_20140424.dat of del select bigint(mbuser_id),cust_name,area_id from nmk.gdi_mbuser_baseinfo_20140424 with ur"

-put 到HDFS
gdi_mbuser_baseinfo_20140424.dat

创建表：
--create 'gdi_mbuser_20140424','pro'

create 'gdi_mbuser_20140424', {NAME => 'pro'},   {SPLITS => ['g', 'm', 'r', 'w']} 


bin/hbase org.apache.hadoop.hbase.mapreduce.ImportTsv -Dimporttsv.separator="," -Dimporttsv.columns=HBASE_ROW_KEY, baseinfo:cust_name, baseinfo:area_id, gdi_mbuser_20140424 /qhbi/gdi_mbuser_baseinfo_20140424.dat











## HBASE SHELL 
###基本操作
`hbase shell` 进入HBASE SHELL,提示符会变成类似:**hbase(main):002:0>**以下命令都在HBASE SHELL执行. 

* 查看状态 :`status`  
* 查看版本:`version`  
* 创建表：`create 'mtable','cf'`  指定表名和列族,多个列族用“,”分隔如：`create 'mtable','cf','cf2'`  
* 插入数据: `put '表名','rowKey','列名','列值'`  
    `put 'mtable','rowKey1','cf:acc_nbr','18797384480'`      
    `put 'mtable','rowKey1','cf:name','migle'`      
列可以动态创建:  列格式是:_列族:列名_  
* 查看数据:  
    **单行:**get 'mtable' , 'rowKey1'  
     get 'mtable','rowKey1','cf:acc_nbr'
     get 'mtable','rowKey1','cf'
    **所有:**scan 'mtable' 
    **所有:**scan 'mtable',{LIMIT=>5}   
* 行数：`count 'mtable'`  
* 更新数据：`put 'mtable','1','cf:acc_nbr','18797384481'`
* 删除数据：`delete 'mtable','1','cf:name'`  
* 清空表： `truncate 'mtable'`  
* 查看表结构：`describe 'mtable'`  
* 修改表结构: 
    1. 添加一个列族：
```     
       disable 'mtable'  
       alter 'mtable', NAME => 'cf2'  
       enable 'mtable'
``` 
    2. 删除列族： 
```
        disable 'mtable'   
        alter 'mtable', 'delete' => 'cf2'  
        enable 'mtable'   
```
* 删除表：
```
disable 'mtable'
drop 'mtable'
```
查询表中行数*大表勿用*  
`count 'mtable', {INTERVAL => 1000, CACHE => 5000}`  

修改TTL    
 `alter 'mtable',{NAME=>'cf2',TTL=>'3600'}`    #单位:秒

修改块压缩算法  
 `alter 'mtable',{NAME=>'cf2',DATA_BLOCK_ENCODING=>'PREFIX_TREE'}` 
 PREFIX_TREE压缩算法后转换的HFile大致是原始文件(用gZIP压缩后)的2倍，不加DATA_BLOCK_ENCODING算法大致是倍
1.9G源文件不用snappy压缩，转换成HFile41.2G
用snappy+prefix_tree之后转换成HFile 3.8G
###region管理  

1. 手动触发major compaction  
  `major_compact 'mtable'`  

2. 移动region 
  `move 'encodeRegionName', 'ServerName'`  

3. 手动split
  `split 'regionName', 'splitKey'`  

4. 


## 系统表
## .META.
  scan 'hbase:meta'

## -ROOT-


## shell
echo "scan 'hbase:meta'" | hbase shell

## 程序开发
### JAVA API




```
db2 "export to /vgopbak/gmi_mbuser_baseinfo.dat of del 
select USER_ID,PRODUCT_NO,CUST_NAME,
    CITY_ID, AREA_CODE, CHANNEL_ID, BRAND_LEVEL1_ID, BRAND_LEVEL2_ID
    , PRODUCT_ID, AGE, SEX_ID,INNET_DATE,IS_STAFF, IS_TEST, IS_GROUP_USER, 
    IS_DATA_CARD, IS_CPE, MOBILE_ONLINE, USIM_4G_FLAG
  from NMK.GMI_MBUSER_BASEINFO_201404 where cust_name is not null and AREA_CODE is not null and CITY_ID is not null and CHANNEL_ID is not null
  and PRODUCT_ID is not null"
```


sed -i 's/ //g' gmi_mbuser_baseinfo.dat
sed -i 's/ //g' 's/"//g' gmi_mbuser_baseinfo.dat

`sed -i -e 's/ //g' -e 's/"//g' gmi_mbuser_baseinfo.dat`    
    

`bin/hbase org.apache.hadoop.hbase.mapreduce.ImportTsv  -Dimporttsv.separator=","  -Dimporttsv.columns=HBASE_ROW_KEY,pro:product_no,pro:cust_name,pro:city_id,pro:area_code,pro:channel_id,pro:brand_level1_id,pro:brand_level2_id,pro:product_id,pro:age,pro:sex_id,pro:innet_date,pro:is_staff,pro:is_test,pro:is_group_user,pro:is_data_card,pro:is_cpe,pro:mobile_online,pro:usim_4g_flag gdi_mbuser_20140424 /vgopbak/gmi_mbuser_baseinfo.dat  `   





##导入数据
1. 使用HTable API
2. Mapper从HDFS导入
3. BulkLoad





## 常用

count
  `count table_name`  效率不高，大表不行

MR统计行数：
  $HBASE_HOME/bin/hbase   org.apache.hadoop.hbase.mapreduce.RowCounter ‘table_name’  