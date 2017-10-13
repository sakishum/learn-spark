##常用 
create database md  DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

##元数据信息

`use information_schema;`
eg:`SELECT table_name,DATA_LENGTH/1024/1024,TABLE_ROWS FROM `TABLES` where TABLE_SCHEMA='md' order by 3 desc ;`

##表名大小写敏感问题
show variables like '%lower_case_table_names%';

`sudo systemctl  stop mariadb` #停服务 
`sudo vim /etc/my.cnf`
添加或修改以下一行

[mysqld]
lower_case_table_names=1
>>	*0*：使用CREATE TABLE或CREATE DATABASE语句指定的大小写字母在硬盘上保存表名和数据库名。名称比较对大小写敏感。在大小写不敏感的操作系统如windows或Mac OS x上我们不能将该参数设为0，如果在大小写不敏感的文件系统上将--lowercase-table-names强制设为0，并且使用不同的大小写访问MyISAM表名，可能会导致索引破坏。
>>  *1*：表名在硬盘上以小写保存，名称比较对大小写不敏感。MySQL将所有表名转换为小写在存储和查找表上。该行为也适合数据库名和表的别名。该值为Windows的默认值。
>> *2*:	表名和数据库名在硬盘上使用CREATE TABLE或CREATE DATABASE语句指定的大小写字母进行保存，但MySQL将它们转换为小写在查找表上。名称比较对大小写不敏感，即按照大小写来保存，按照小写来比较。注释：只在对大小写不敏感的文件系统上适用! innodb表名用小写保存。

`sudo systemctl  start mariadb`  #启服务


 
