### 固定ip设置

    编辑：*/etc/sysconfig/network-scripts/ifcfg-e....*

### 设置主机名

        `hostnamectl set-hostname  vm-centos-00`


### ssh互信配置

    各个主机依次生成密钥： ssh-keygen -t rsa  
    
    其它三台公钥复制到130
    scp migle@192.168.254.131:/home/migle/.ssh/id_rsa.pub ./a.pub
    scp migle@192.168.254.132:/home/migle/.ssh/id_rsa.pub ./b.pub
    scp migle@192.168.254.133:/home/migle/.ssh/id_rsa.pub ./c.pub
    
    合并后依次分发到其它机器

    cat id_rsa.pub >> authorized_keys   
    cat ./a.pub >> authorized_keys
    cat ./b.pub >> authorized_keys
    cat ./c.pub >> authorized_keys

scp ./authorized_keys  migle@192.168.254.133:/home/migle/.ssh/authorized_keys 

### 清理多余内核 

1、首先列出系统中正在使用的内核:
uname -a
 
2、查询系统中全部的内核
rpm -qa | grep kernel

3.删除不需要的
yum remove kernel-3.10.0-229.4.2.el7.x86_64
yum remove kernel-tools-libs-3.10.0-229.4.2.el7.x86_64

