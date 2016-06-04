## 数据类型
### 数字类型
类型		   | 长度			     |   范围
---         |     ---            |  ---
TINYINT	|1-byte signed integer|from -128 to 127
SMALLINT|2-byte signed integer|from -32,768 to 32,767
INT 	|4-byte signed integer			|from -2,147,483,648 to 2,147,483,647
BIGINT 	|8-byte signed integer			|from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807
FLOAT	|4-byte single precision floating point number|
DOUBLE	|8-byte double precision floating point number|
DECIMAL	| |

### 布尔

BOOLEAN					|TRUE/FALSE |

-------
## 日期类型
类型	| 长度			|   范围
--      |--             |--
DATE|   |
TIMESTAMP | |

----

### 字符串

类型	| 长度			|   范围
--      |--             |--
STRING  |  |sequence of characters in a specified character set with a maximum length
VARCHAR	| 65535 |between 1 and 65355	sequence of characters in a specified character set
CHAR	| 255 |The maximum length is fixed at 255	sequence of characters in a specified character set with a defined length
### 二进制

类型	| 长度			|   范围
--      |--             |--
BINARY| |		a sequence of bytes

### 关于NULL值
*不同的SerDe可以做不同处理*

### 复杂类型
类型	| 长度			|   范围
--      |--             |--
ARRAY| |
MAP  | |
STRUCT  | |
UNIONTYPE  |  |


[Hive Data Types](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types)


## 类型转换

1. 隐式转换 [Allowed Implicit Conversions](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types#LanguageManualTypes-Overview)

2. 显式转换 : cast(stype as ttype)


## 常用函数
### 相关命令:
    show functions; --列出所有函数  
    desc function fun_name --查看该函数说明  

### 数据转换

### 日期处理

### 条件处理
1. COALESCE(a1, a2, ...) *Returns the first non-null argument*

2. IF(expr1,expr2,expr3)  *If expr1 is TRUE (expr1 <> 0 and expr1 <> NULL) then IF() returns expr2; otherwise it returns expr3. IF() returns a numeric or string value, depending on the context in which it is used.*

3. CASE WHEN a THEN b [WHEN c THEN d]* [ELSE e] END   *When a = true, returns b; when c = true, return d; else return e* #desc function case

4. CASE a WHEN b THEN c [WHEN d THEN e]* [ELSE f] END *When a = b, returns c; when a = d, return e; else return f* #desc function when

## HiveQL
语法基本和SQL一致

### mapjoin
>>可以通过 /*+ MAPJOIN(alias)*/ 或
set hive.auto.convert.join=true 

1. Local work:
- read records via standard table scan (including filters and projections) from source on local machine
- build hashtable in memory  
- write hashtable to local disk  
- upload hashtable to dfs  
- add hashtable to distributed cache

2. Map task
- read hashtable from local disk (distributed cache) into memory
- match records' keys against hashtable
- combine matches and write to output
3. No reduce task



<https://cwiki.apache.org/confluence/display/Hive/LanguageManual+JoinOptimization#LanguageManualJoinOptimization-PriorSupportforMAPJOIN>

### 多表插入
Multi Table/File Inserts
The output of the aggregations or simple selects can be further sent into multiple tables or even to hadoop dfs files (which can then be manipulated using hdfs utilities). For example, if along with the gender breakdown, one needed to find the breakdown of unique page views by age, one could accomplish that with the following query:
FROM pv_users
INSERT OVERWRITE TABLE pv_gender_sum
    SELECT pv_users.gender, count_distinct(pv_users.userid)
    GROUP BY pv_users.gender
 
INSERT OVERWRITE DIRECTORY '/user/data/tmp/pv_age_sum'
    SELECT pv_users.age, count_distinct(pv_users.userid)
    GROUP BY pv_users.age;
The first insert clause sends the results of the first group by to a Hive table while the second one sends the results to a hadoop dfs files.
Dynamic-Partition Insert
In the previous examples, the user has to know which partition to insert into and only one partition can be inserted in one insert statement. If you want to load into multiple partitions, you have to use multi-insert statement as illustrated below.

### 数据导入

    语法:
    LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)]

例：

    `load data inpath '/hdata/dw_user_info/' into table dw_user_info; #从hdfs导入`
    `load data local inpath '/hdata/dw_user_info/' into table dw_user_info; #从本地文件系统导入`

### 数据导出
语法：
例：

    insert overwrite local directory 'test_2' select * from test_2;
    insert overwrite local directory 'test_2' ROW FORMAT DELIMITED  FIELDS TERMINATED BY ',' select * from test_2;
    insert overwrite directory '/user/migle/data/test_2' select * from test_2;

 >>导出数据默认列分隔符为\x01(^A)

### 有关dual
可以参照oracle创建dual，方便查询常量及测试函数:

    create table dual(dummy String);#创建表结构
    insert into dual values("X");   #插入一条数据

    select from_unixtime(unix_timestamp(),'yyyyMMdd')  from dual;
    select from_unixtime(unix_timestamp())  from dual;

### 排序

- [ ]   order by   
- [ ]   sotred by   

### left semi join

## 表
### 外部分表

### 分区表

### 桶表
https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL+BucketedTables

1. 高效查询：mapside joins
2. 方便高效的取样操作
### 存储格式

### 数据存存储单元
Databases,Table,Partitions,Buckets
<https://cwiki.apache.org/confluence/display/Hive/Tutorial#Tutorial-DataUnits>

## 常用命令

1. show tables;
2. show create table tab_name;  //查看建表语句
2. show tables 'name*';
3. show functions
4. desc function unix_timestamp; #查看函数说明
5. ! <command> #Executes a shell command from the Hive shell.
6. dfs <dfs command> #Executes a dfs command from the Hive shell.

## export/import
https://cwiki.apache.org/confluence/display/Hive/LanguageManual+ImportExport


## 其它

### 取样
Sampling可以在全体数据上进行采样，这样效率自然就低，它还是要去访问所有数据。而如果一个表已经对某一列制作了bucket，就可以采样所有桶中指定序号的某个桶，这就减少了访问量

        select *  from dw_user_info tablesample(bucket 1 out of 32)


## 部署安装
### 系统架构
### 运行机制
### 连接方式
CLI


### 命令行参数
CLI or beeplin

1. set            //显示所有参数值
2. set key=value  //设置参数值
3. 更多:[命令行参数](https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Commands)



------------------------------------

###正则表达式选择列  (REGEX Column Specification)
set hive.support.quoted.identifiers=none
默认值为column
select `d[0-9]+` from tab1;  --选择列名以d开头，后带数字的列

### 日志

/tmp/hive/$USER/

## 自定义函数
## 自定义格式

## 性能优化
map的输入是上一阶段的reduce输出

1. 不是所有的语句都会转化成MR运行比如：explain select * from dw_user_info limit 10;
2. 有些任务可能只有M没有R



### explain

例:
    `explain select count(*) from dw_user_info ;`

### map端聚合
map端聚合:默认已经为true  `hive.map.aggr=true`

### Mapjoin

### 合并小文件


### 数据倾斜处理
因为受一个或少量几个任务处于reduce阶段影响，而使作业进度长时间维持在快要完成的状态(比如99%)，这种情况一般来说是发生了数据倾斜导致。导致数据倾斜的原因大致有：业务特性比如有些key本身就分布不均、建表时考虑不周。而解决数据倾斜问题的根本在于让数据均匀的分布到各个结点上处理，如果因为业务特性就使得key分布存在倾斜，可以通过在key上加上随机数据等手段，使数据均匀分布。

1. join
如果小表join大表，但key很集中，可以使用mapjoin
如果大表join大表，存在大量null值，可以将null值过渡后union到非空值的结果中，也可以给null重新分配一个随机值后做关联

select * from dw_gprs_flow a left join dw_user_info b on a.acc_nbr  = b.acc_nbr;

select * from dw_gprs_flow a left join dw_user_info b on case when a.acc_nbr is null then rand() else a.acc_nbr end = b.acc_nbr;

select * from dw_gprs_flow a left join dw_user_info b on  a.acc_nbr is not null and a.acc_nbr  = b.acc_nbr
union all
select * from dw_gprs_flow a left join dw_user_info b a.acc_nbr  = b.acc_nbr;


2.distinct
  如果存在大量NULL值的情况，可以将NULL值的情况单独处理，如果是计算count distinct，可以不用处理，直接过滤，在最后结果中加1。
  如果还有其他计算，可以先将值为空的记录单独处理，再和其他计算结果进行union。

3 . group by：
hive.groupby.skewindata=true
有数据倾斜的时候进行负载均衡，当选项设定为 true，生成的查询计划会有两个 MR Job。第一个 MR Job 中，Map 的输出结果集合会随机分布到 Reduce 中，每个 Reduce 做部分聚合操作，并输出结果，这样处理的结果是相同的 Group By Key 有可能被分发到不同的 Reduce 中，从而达到负载均衡的目的；第二个 MR Job 再根据预处理的数据结果按照 Group By Key 分布到 Reduce 中（这个过程可以保证相同的 Group By Key 被分布到同一个 Reduce 中），最后完成最终的聚合操作。

### 总结
减少数据量：列裁减、分区
减少job:SQL优化
任务能尽量均的分配到各结点上执行：数据倾斜处理

## 对比 RDMS


## 源码构建


## 与HBase集成



### spark


# 参考资料

1. [Hive Documentation](https://cwiki.apache.org/confluence/display/Hive/Home)
2. http://www.tuicool.com/articles/qyUzQj



#TODO
