## 关于  
这一个基于CentOS7和Oracle Java8的Docker镜像，Spark版本为*spark-1.6.3-bin-hadoop2.6*   

# 如何启动一个Spark集群

可以手动启动多个镜像，在其中一个镜像中启动master其它的启动worker,当然也可以将这个过程包装在一个shell中实现 ，但更简单的方法是使用*docker-compose* ，下面会介绍到。 

你可以不是需要很了解docker,但需要docker已经被正确的安装并可用，
如果有问题，请抽小半个小时看看这里<http://wiki.jikexueyuan.com/project/docker/articles/basics.html>
或着试试运气直接用[docker-toolbox](https://www.docker.com/products/docker-toolbox)安装
 
>> 如果是在MacOS可能会出现创建虚拟机失败的情况，你需要手工创建一下虚拟机
`docker-machine create --virtualbox-hostonly-cidr "10.10.10.1/24" default`  #创建
`docker-machine start default`      #启动
`eval $(docker-machine env default)`  #设置环境变量


>>如果想使用不同的spark版本，可以在这个基本上修改https://github.com/longforfreedom/dockerfile-spark
>>从dockfile构建进用 `docker build -t migle/spark .`  

## 手动方式 
### 启动master

 `docker run --name=master -p 8080:8080 -p7077:7077 -p 6066:6066 -p 8081:8081 -i -t migle/spark`

### 启动worker
>> 想要几个Worker就执行几次下面这两条

``` 
        ##1.  启动镜像
        docker run --link=master -i -t migle/spark 
        ##2. 在镜像中启动worker
        spark-class org.apache.spark.deploy.worker.Worker spark://master:7077

```  

## docker-compose 方式
>> docker-compose start default  docker-compose.yml ###启动

新建文件：docker-compose.yml,内容如下  

```
version: "2"

services:
  master:
    image: migle/spark
    command: spark-class org.apache.spark.deploy.master.Master 
    hostname: master
    ports:
      - "6066:6066"
      - "7070:7070"
      - "8080:8080"
      - "50070:50070"
  worker:
    image: migle/spark
    command: spark-class org.apache.spark.deploy.worker.Worker spark://master:7077
    environment:
      SPARK_WORKER_CORES: 1
      SPARK_WORKER_MEMORY: 1g
    links:
      - master
```
1. 启动
`docker-compose up -d`  
docker-compose ps #查看情况  
>> 启动后只有一个Master,一个Worker,

启动两个worker:  
`docker-compose scale worker=2`

## 其它
与Linux系统相比MacOS和windows的docker其实是在virtualbox中的Linux中运行的，所以MacOS和Windows中的Docker镜像的端口映射是相对于虚拟机中的Linux而言的，所以如果要从MacOS和Windows中连接到Docker其实是需要先连接到虚拟机中的Linux的，   
比如本文使用系统为MacOS,virtualbox分配的ip为10.10.10.100，
在MacOS中启动spark-shell连接到docker中的master
`spark-shell --master spark://10.10.10.100:7077`

>> ！master启动时将7077端口映射到了宿主机的7077



## 致谢
本文基本都是参照： https://github.com/SingularitiesCR/spark-docker 估计他看不到，但还是感谢作者。
