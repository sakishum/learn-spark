FROM migle/oracle-jdk8
MAINTAINER migle <longforfreedom@gmail.com>

ENV JAVA_HOME /usr/java/default

WORKDIR /opt

#download from spark website
#aliyun镜像在dockerhub上构建下载失败
#RUN wget https://mirrors.aliyun.com/apache/spark/spark-1.6.3/spark-1.6.3-bin-hadoop2.6.tgz
RUN wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.3-bin-hadoop2.6.tgz

#upload local spark
#COPY ./spark-1.6.3-bin-hadoop2.6.tgz /opt

RUN tar xfvz /opt/spark-1.6.3-bin-hadoop2.6.tgz
RUN rm -r /opt/spark-1.6.3-bin-hadoop2.6.tgz

# Set home
ENV SPARK_HOME=/opt/spark-1.6.3-bin-hadoop2.6

#set spark/bin
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin

# Ports
EXPOSE 6066 7077 8080 8081


