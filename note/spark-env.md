##从源码编译

./make-distribution.sh --name my-spark --tgz -Psparkr -Phadoop-2.6 -Phive -Phive-thriftserver -Pyarn


##standlone

1. 创建slaves文件，添加worker节点
2. 复制spark到其它节点
3. ./sbin/start-all.sh  启动master及worker
4. 启动成功后可以在http://vm-centos-00:8080/查看集群信息
