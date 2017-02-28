

export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop   
>>spark-env.sh中设置不启作用？
./bin/spark-sql --master yarn-client  --jars /opt/hive-2.0.0/lib/mysql-connector-java-5.1.38-bin.jar