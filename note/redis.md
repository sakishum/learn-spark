##安装
```shell
wget http://download.redis.io/releases/redis-3.2.1.tar.gz
tar xzf redis-3.2.1.tar.gz
cd redis-3.2.1
make
```

##启动

1. 服务端：`src/redis-server`  
2. 客户端：`src/redis-cli`  

## 入门操作



命令不区分大小写，但key与值是区分大小写的
返回值，一般情况下成功为1或OK，不成功为0，失败或出差为值负值


## 数据类型
reids中数据用一个key和对应value来表示，key是一个字符串，value支持string（字符串）、hash（哈希）、list（列表）、set（集合）及zset(sorted set：有序集合)等多种数据类型。

- [ ] string 
string是redis中最基本的数据类型，可以表示字符串、数字、一段HTML页面、JSON等文本信息，也可以是二进制数据比如一个jpeg图片(编码后还是字符串，所以本质上还是符串，但存放图片貌似不是个好方式)。可以对期进行修改(set)、追加(append)，指定偏移量的读取(getrange)和修改(setrange)操作。最大512MB。

- [ ] hash
hash由field和关联的value组成的map。field和value都是字符串,最多可保存Math.pow(2,32)-1个键值对。
操作命令均以h开头如hset,hget,hlen,hdel等
- [ ] list
按插入顺序排序的字符串元素的集合，可以有重复元素，最多可保存Math.pow(2,32)-1个键值对。 


-[ ] set

- [ ] orted sets  
- [ ] bitmap
- [ ] hyperloglogs 
- [ ] geospatial indexes 
 
[redis数据类型介绍](http://www.redis.cn/topics/data-types-intro.html)

## 备份及恢复
`save` 创建当前数据库备份
如果需要恢复数据，只需将备份文件 (dump.rdb) 移动到 redis 安装目录并启动服务即可。
>>`CONFIG GET dir`获取目录可以使用 
>>`bgsave` 后台执行save





## API

- [ ] TODO


## 分布式
- [ ] TODO

## 集群
:hash环


## 配置


## 参考资料
1. [redis 教程](http://www.redis.net.cn/tutorial/3501.html)
2. [redis数据类型介绍](http://www.redis.cn/topics/data-types-intro.html)
3. http://www.redis.cn/topics/mass-insert.html
