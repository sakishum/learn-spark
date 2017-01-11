## 常用命令

    create database if not exists mdb; #创建数据库  
    use mdb; 选择当前数据库  
    drop database if exists mdb;  

    show databases;
    show tables; 
    desc table_name;  

## 内部表
数据文件在hive管理的目录下  

    CREATE  TABLE test_1(ord INT,name STRING,money FLOAT)
    ROW FORMAT DELIMITED  FIELDS TERMINATED BY ',';  

## 外部表
相当于给数据文件做个映射，删除表并不会删除数据文件,目录下文件变化时，表中的数据也相应发生变化。  

    CREATE EXTERNAL TABLE test_et_1(ord INT,name STRING,money FLOAT)
    ROW FORMAT DELIMITED  FIELDS TERMINATED BY ','
    LOCATION '/user/migle/data1/';

>>location指定的是目录，如果目录不存会自动创建，可以是hdfs中的目录也可以是本地系统(eg: file:////home/migle/data2)，hive会把整个目录下的文件都加载到表中

    CREATE EXTERNAL TABLE test_hive_1(ord INT,name STRING,money FLOAT)
    ROW FORMAT DELIMITED  FIELDS TERMINATED BY ','

##分区表
创建表时需要指标分区列，导入数据时候需要指定分区  


    create table test_p1(ord INT,name STRING,money FLOAT)
    partitioned by (dt date,city string)
    ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

    load data  inpath '/user/migle/data1/test_hive_1.dat' into table test_p1 partition(dt='2016-04-01',city='CD');

动态插入分区
    insert overwirte table T_TABLE_NAME partition(pcity_id)
    select c1,c2,pcity_id from S_TABLE_NAME

    set hive.exec.dynamic.partition.mode=nonstrict;
    insert into table test_p1 partition(dt,city) 
    select  *  from test_p1 where ord=16;
 
select中的字段与目标表分区字段要一致
 
 >>加载完成后原始数据文件会被删除

    show partitions test_p1; #查看所有分区`
    ALTER TABLE table_name DROP partition_1, partition_2,...   #删除分区
   
    添加分区
    ALTER TABLE table_name ADD [IF NOT EXISTS] partition_spec [ LOCATION 'location1' ] partition_spec [ LOCATION 'location2' ] ...
partition_spec:
  : PARTITION (partition_col = partition_col_value, partition_col = partiton_col_value, ...)

ALTER TABLE tab1 ADD 
PARTITION (dt='20131202') location '/user/hive/warehouse/tab1/part0002' 




    select * from test_p1 --where city='CD';

##桶表
create table test_b1 (ord INT,name STRING,money FLOAT)
clustered by (ord) into 10 buckets;

//TODO

###数据导入&导出

--创建外部表
--删除表时，表的元数据会被删除掉，但是数据不会被删除
--如果数据被多个工具（如pig等）共享，可以创建外部表
create external table if not exists sopdm.test1(
name string comment ‘姓名’,
salary float comment ‘薪水’)
comment ‘这是一个测试的表’
tblproperties(‘creator’=’me’,’created_at’=’2014-11-13 09:50:33’)
location ‘/user/hive/warehouse/sopdm.db/test1’
 
 
--分区表
create table if not exists sopdm.test1(
name string comment ‘姓名’,
salary float comment ‘薪水’)
comment ‘这是一个测试的表’
partitioned by(country string,state string)
STORED AS rcfile
tblproperties(‘creator’=’me’,’created_at’=’2014-11-13 09:50:33’)
location ‘/user/hive/warehouse/sopdm.db/test1’
 
 
--查看表中存在的所有分区
show partitions table_name;
--查看表中特定分区
show partitions table_name partition(country=’US’);
 
 
--可以在表载入数据的时候创建分区
load data local inpath ‘${env:HOME/employees}’
into table employees
partition(country=’US’,state=’CA’);
 
 
--删除表
drop table if exists table_name;
 
 
--修改表-表重命名
alter table old_table_name rename to new_table_name;
 
--增加分区
alter table table_name add if not exists partition(year=2011,month=1,day=1)
location ‘/logs/2011/01/01’;
 
--修改分区存储路径
alter table table_name partition(year=2011,month=1,day=2)
set location ‘/logs/2011/01/02’;
 
--删除某个分区
alter table table_name drop if exists partition(year=2011,month=1,day=2);
 
--修改列信息
alter table table_name
change column old_name new_name int
comment ‘this is comment’
after severity;         --字段移到severity字段之后（移动到第一个位置，使用first关键字）
 
--增加列
alter table table_name add columns(app_name string comment ‘application name’);
 
--删除或者替换列
alter table table_name replace columns(hms int comment ‘hhh’);
 
--修改表属性
alter table table_name set tblproperties(‘notes’=’this is a notes’);
 
--修改存储属性
alter table table_name partition(year=2011,month=1,day=1) set fileformat sequencefile;
 
--指定新的SerDe,并指定SerDe属性
alter table table_name
set serde “com.example.JSONSerDe”
with serdeproperties(‘prop1’=‘value1’, ‘prop2’=‘value2’);
 
--增加执行“钩子”——当表中存储的文在hive之外被修改了，就会触发钩子的执行
alter table table_name touch partition(year=2012,month=1,day=1);
 
--将分区内的文件打成hadoop压缩包文件，只会降低文件系统中的文件数，减轻NameNode的压力，而不会减少任何的存储空间
--使用unarchive替换archive起到反向操作
alter table table_name archive partition(year=2012,month=1,day=1);
 
--防止分区被删除和被查询(使用enable替代disable可以起到反向的操作目的)
alter table table_name partition(year=2012,month=1,day=1) disable no_drop;
alter table table_name partition(year=2012,month=1,day=1) disable offline;
 
 
--向管理表中装载数据
-- inpath为一个目录，而且这个路径下不可以包含任何文件夹
load data local inpath ‘${env:HOME}/table_name’
overwrite into table table_name
partition(country=’US’);
 
 
--通过查询语句向表中插入数据
--overwrite是覆盖，into是追加
insert overwrite table table_name
partition(country=’US’)
select * from table_name2 tn where tn.cnty=’US’
 
 
--高效方式-查询语句插入多个分区
from table_name2 tn
insert overwrite table table_name
partition(country=’US’,state=’OR’)
         select * where tn.cnty=’US’ and tn.st=’OR’
insert overwrite table table_name
partition(country=’US’,state=’CA’)
         select * where tn.cnty=’US’ and tn.st=’CA’
 
 
--动态插入分区
--hive根据select语句最后2列确定分区字段country和state的值（根据位置）
insert overwrite table table_name
partition(country,state)
select …,se.cnty,se.st
from employees se;
 
--动态和静态分区结合
--country为静态分区，state为动态分区（静态分区必须在动态分区之前）
insert overwrite table table_name
partition(country=‘US’,state)
select …,se.cnty,se.st
from employees se
where se.cnty=’US’;
 
 
--单个查询语句中创建表并加载数据
create table table_name1
as select name,salary,address from table_name2 where state=’CA’;
 
 
--导出数据——拷贝文件
--如果数据文件恰好是用户需要的格式，那么只需要简单的拷贝文件或文件夹就可以。
hadoop fs –cp source_path target_path
 
 
--导出数据
insert overwrite local directory ‘/tmp/employees’
select name,salary,address from employees se where se.state=’CA’
 
--导出数据到多个输出文件夹
from employees se
insert overwrite local directory ‘/tmp/or_employees’
         select * se where se.cty=’US’ and se.st=’OR’
insert overwrite local directory ‘/tmp/ca_employees’
         select * se where se.cty=’US’ and se.st=’CA’





#UDF
//TODO
1. 当前会话有效
add jar /opt/hive-2.0.0/udf/hiveudf.jar;
create temporary function value as 'HiveValue';
create temporary function recode as 'Recode';

2. 创建永久函数

在HIVE_HOME下创建auxlib将jar包此目录中 
在hdfs-site.xml中hive.aux.jars.path指定jar包位置
在hive --auxpath=xXX   中指定

create function value as 'HiveValue';

有点问题！！！！
CREATE FUNCTION [db_name.]function_name AS class_name
  [USING JAR|FILE|ARCHIVE 'file_uri' [, JAR|FILE|ARCHIVE 'file_uri'] ];

CREATE FUNCTION myfunc AS 'myclass'  USING JAR 'hdfs:///user/hive/lib/hiveudf.jar';
CREATE FUNCTION value AS 'HiveValue' USING JAR 'hdfs:///user/hive/lib/hiveudf.jar';

GRANT ALL ON URI 'hdfs:///user/hive/lib/hiveudf.jar' TO ROLE hive
并不是所有的Hive查询都会生成MR，比如:select * from TAB1
有些查询只生成Map不会生成Reduce




from tab1
insert overwrite table tab5 select * from tab1
insert overwrite local directory "~/test"  select * from tab1;

#Hive优化

多表关联时，如果关联键为同一字段，则会转化成一个Job,否则会转化成多个Job

LEFT SEMI JOIN 是 IN/EXISTS 子查询的一种更高效的实现。Hive 当前没有实现 IN/EXISTS 子查询，所以你可以用 LEFT SEMI JOIN 重写你的子查询语句。LEFT SEMI JOIN 的限制是， JOIN 子句中右边的表只能在 ON 子句中设置过滤条件，在 WHERE 子句、SELECT 子句或其他地方过滤都不行。
  SELECT a.key, a.value
  FROM a
  WHERE a.key in
   (SELECT b.key
    FROM B);
可以被重写为：
   SELECT a.key, a.val
   FROM a LEFT SEMI JOIN b on (a.key = b.key)

RDBMS VS  Hive
SQL 对 HQL
存储对比
延迟

Hive有限的索引，有限的Update操作！！！
