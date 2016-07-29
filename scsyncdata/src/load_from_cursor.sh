###在JAVA程序中嵌入shell脚本实现，数据同步骤使用db2 load from cursor
#!/bin/bash 


##################################################
#使用db2 load from cursor 同步数据
##################################################
load_from_cursor(){
    db2 connect to $target_db_node user $target_db_user using $target_db_passwd
    db2 "DECLARE $cursor_name CURSOR DATABASE $source_db_node USER $source_db_user USING $source_db_passwd  FOR $sql"
    db2 "LOAD FROM $cursor_name OF CURSOR MESSAGES $log INSERT INTO $target_table" 
    db2 connect reset
}
 
#判断表存在
#删除表
drop_table(){
    db2 connect to $target_db_node user $target_db_user using $target_db_passwd
    db2 "drop table $target_table"
    db2 connect reset
}
   
#创建表


#判断表是否需要同步


#生成实际目标客户群


#加载到redis
