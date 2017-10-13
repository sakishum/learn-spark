## 安装
1. 安装Erlang
`vim /etc/yum.repos.d/rabbitmq-erlang.repo`  

添加以下内容
```
[rabbitmq-erlang]
name=rabbitmq-erlang
baseurl=https://dl.bintray.com/rabbitmq/rpm/erlang/20/el/7
gpgcheck=1
gpgkey=https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
repo_gpgcheck=0
enabled=1
```
安装erlang  
`sudo yum install erlang` 

2. 安装rabbitmq  
在<https://www.rabbitmq.com/install-rpm.html>找到相应版本下载  
下载:  
    `wget https://dl.bintray.com/rabbitmq/rabbitmq-server-rpm/rabbitmq-server-3.6.12-1.el7.noarch.rpm`  
安装：  
`sudo rpm --import https://dl.bintray.com/rabbitmq/Keys/rabbitmq-release-signing-key.asc`  
`sudo yum install rabbitmq-server-3.6.12-1.el7.noarch.rpm`


>>`chkconfig rabbitmq-server on` #系统启动时自动启动 
>>`sudo systemctl start rabbitmq-server` #手工启动


## 管理
1. `rabbitmqctl status` 
2. `rabbitmqctl  add_user  username  pwd`
3. `rabbitmqctl list` 
## 开发

