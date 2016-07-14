##安装
```shell
wget http://download.redis.io/releases/redis-3.2.1.tar.gz
tar xzf redis-3.2.1.tar.gz
cd redis-3.2.1
make
```

##启动

1. 服务端：`src/redis-server redis.conf`  
2. 客户端：`src/redis-cli`  
>>远程连接：关闭protected-mode或设置密码：
1. 修改服务器的redis.conf文件，添加*bind 192.168.99.130*  192.168.99.130对应实际的ip地址。连接命令`连接命令:` ./src/redis-cli -h 192.168.99.130``
2. 临时关闭protected-mode,在服务器本来地客户端连接后执行 `CONFIG SET protected-mode no` 
3. 带参数*--protected-mode no*重启服务器
4. 在服务器本来地客户端连接后执行`CONFIG set requirepass "redispass"`设置密码，`CONFIG rewrite`保存到配置文件中，下次重启继续生效
 连接命令：`./src/redis-cli -h 192.168.99.130 -a redispass`


## 常用操作

命令不区分大小写，但key与值是区分大小写的
返回值，一般情况下成功为1或OK，不成功为0，失败或出差为值负值

批量执行


flushall 删除所有数据库的所有key
flushdb 删除当前数据库的所有key

http://www.redis.cn/commands.html

CONFIG get requirepass

## 数据类型
reids中数据用一个key和对应value来表示，key是一个字符串，value可以是string（字符串）、hash（哈希）、list（列表）、set（集合）及zset(sorted set：有序集合)等数据类型。

- [ ] string 
string是redis中最基本的数据类型，可以表示字符串、数字、一段HTML页面、JSON等文本信息，也可以是二进制数据比如一个jpeg图片(编码后还是字符串，所以本质上还是符串，但存放图片貌似不是个好方式)。可以对期进行修改(set)、追加(append)，指定偏移量的读取(getrange)和修改(setrange)操作。最大512MB。

- [ ] hash
hash由field和关联的value组成的map。field和value都是字符串,最多可保存Math.pow(2,32)-1个键值对。
操作命令均以h开头如hset,hget,hlen,hdel等

- [ ] list
按插入顺序排序的字符串元素的集合，可以有重复元素，最多可保存Math.pow(2,32)-1个元素。 

- [ ] set  
Set是string类型的无序集合。集合成员是唯一的，不能有重复元素，Redis 中的Set是通过哈希表实现的，所以添加，删除，查找的复杂度都是O(1),最多可保存Math.pow(2,32)-1个元素。

- [ ] sorted set
类似Set 不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。zset的成员是唯一的,但分数(score)却可以重复

- [ ] hyperloglog 
- [ ] bitmap
- [ ] geospatial indexes 
 
[redis数据类型介绍](http://www.redis.cn/topics/data-types-intro.html)

## 备份及恢复
`save` 创建当前数据库备份
如果需要恢复数据，只需将备份文件 (dump.rdb) 移动到 redis 安装目录并启动服务即可。
>>`CONFIG GET dir`获取备份文件目录 
>>`bgsave` 后台执行save





## API

- [ ] JAVA
- [ ] Python


## 集群&&分布式
- [ ] TODO
[Redis 集群方案介绍了](http://www.open-open.com/lib/view/open1465520425935.html)



##主从复制
修改slave配置文件redis.con添加如下内容：
```
# slaveof <masterip><masterport>
slaveof  192.168.99.130  6379
#如果master需要需要密码认证时
#masterauth <password>
masterauth   redispass
```
启动slave后进行难，在master中写，slave中读取
<http://redis.cn/topics/replication.html>

###客户端分片
一致性HASH
http://blog.jobbole.com/102630/
http://blog.jobbole.com/80334/

## 参考资料
1. [redis 教程](http://www.redis.net.cn/tutorial/3501.html)
2. [redis数据类型介绍](http://www.redis.cn/topics/data-types-intro.html)
3. http://www.redis.cn/topics/mass-insert.html
