- [ ] spark-sql on hive
复制hive-site.xml到spark的conf目录下
spark-env.sh中添加`export SPARK_CLASSPATH=$HIVE_HOME/lib/mysql-connector-java-5.1.38-bin.jar:$SPARK_CLASSPATH`
./spark-shell运行

show tables;
select acc_nbr ,sum(g4_flow) from dw_gprs_flow group by acc_nbr limit 10;
.......
>>日志特别多，可以修改log4j.properites文件中的日志级别
- [ ] spark-sql on hive 运行在 yarn模式下
- [ ] spark-shell 运行在yarn模式下
./spark-shell --master yarn-client  --executor-memory 1G  --num-executors 10