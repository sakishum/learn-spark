import redis.clients.jedis.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by migle on 2016/6/28.
 */
public class RedisAPIDemo {


    /**
     * string的基本操作
     */
    private static void aboutString(Jedis jedis) {
        /*******************STRING************************/
        //set name m1234
        jedis.set("name", "m1234");

        //strlen name
        jedis.strlen("name");

        //set name m456 EX 5 XX
        //NX:如果key已经存在则不做修改，不存在时则新增  XX:如果key存在则修改，不存在时不会新增
        //EX|PX 过期时间单位，EX:秒,PX:毫秒
        jedis.set("title", "m456", "XX", "EX", 5L);

        //getset title m899
        //设置成新值并返回旧值，没旧值时(key不存在时)返回nil
        jedis.getSet("title", "m899");

        //jedis.set("name-cn".getBytes(),"亚信".getBytes("utf-8"));

        //System.out.println(jedis.get("name-cn"));

        //mget name title
        jedis.mget("name", "title");

        //append   name   789
        //key不存在时，append命令会新增
        jedis.append("name", "789");

        //getrange name 0 2
        jedis.getrange("name", 0L, 2L);

        //setrange title 2 test
        jedis.setrange("title", 2, "test");

        jedis.set("times", "10");

        //incr key //++1
        jedis.incr("times");
        //incrby key val   //加val
        jedis.incrBy("times", 20);
        //incrbyfloat key float  //浮点增量
        jedis.incrByFloat("times", 2.3);

        //针对整数
        //decr key
        //decrby key 5
        jedis.set("tt", "5");
        jedis.decr("tt");
        jedis.decrBy("tt", 5);

        //TODO 有什么使用场景
        //jedis.setbit("name","")

        //打印所有string类型的key及值
        Set<String> keys = jedis.keys("*");
        keys.stream()
                .filter(key -> jedis.type(key).equals("string"))
                .forEach(key -> System.out.println(key + ":" + jedis.get(key)));

    }

    /**
     * hash的基本操作
     */
    private static void aboutHash(Jedis jedis) {
        /*******************hash*********************/
        //MNOTE:hash好像不能把过期时间单独设置某一field上，只能设置在key上

        //hmset key f1 v1 f2 v2 f3 v3
        Map<String, String> map = new HashMap<>();
        map.put("name", "redis");
        map.put("type", "memory db");
        map.put("lang", "c");
        map.put("maxclients", "30000");
        map.put("version", "2.6");

        jedis.hmset("redis", map);

        //hset key f1 v1
        jedis.hset("redis", "os", "all");

        //hsetnx key f1 vv1
        //如果f1已经存在则不做修改，不存在时则新增
        jedis.hsetnx("redis", "os", "all");
        //hget key f1
        System.out.println(jedis.hget("redis", "os"));

        //hmget key f1 f2
        jedis.hmget("redis", "name", "type").stream().forEach(v -> System.out.println(v));

        //hgetall redis
        jedis.hgetAll("redis").forEach((k, v) -> System.out.println(k + ":" + v));

        //hkeys key
        jedis.hkeys("redis");
        //hvals key
        jedis.hvals("redis");


        //hlen key   //哈希表大小
        jedis.hlen("redis");

        //hdel key f1....fn  // 删除一个或多个哈希表字段
        jedis.hdel("redis", "lang");

        //hexists key f1; //查看哈希表中指定的字段是否存在
        jedis.hexists("redis", "os");

        //hincrBy key f1 increment
        //hincrByFloat key f1 increment
        jedis.hincrBy("redis", "maxclients", 100L);
        jedis.hincrByFloat("redis", "version", 0.1);


//key cursor [MATCH pattern] [COUNT count]
        ScanResult<Map.Entry<String, String>> scan ;
        String cursor="0";
        do {

            scan = jedis.hscan("redis", cursor);
            scan.getResult().forEach(k -> System.out.println(k.getKey()+":"+k.getValue()));
            cursor = scan.getStringCursor();
        }while(!cursor.equals("0"));

    }

    /**
     * list的基本操作
     */
    private static void aboutList(Jedis jedis) {
        //lpush key e1  //列表头增加，key不存在时新增key
        //lpushx key e1  //key存在时列表头增加，不存在时不做操作
        jedis.lpush("users", "u1");
        jedis.lpushx("users", "u1");

        //rpush key e2  //列表尾增加，key不存在时新增key
        //rpushx key e1  //key存在时列表尾增加，不存在时不做操作
        jedis.rpush("users", "u5");
        jedis.rpushx("users", "u5");


        //lset key index value //通过索引设置列表元素的值
        jedis.lset("users", 2, "ux");

        //rpop key  //移除并且返回key对应list的最后一个元素
        //lpop key  //移除并且返回key对应list的第一个元素
        //brpop key1 [key2 ] timeout 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        //blpop key1 [key2 ] timeout 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        jedis.rpop("users");
        jedis.lpop("users");

        //rpoplpush source destination //从列表中弹出一个值，将弹出的元素插入到另外一个列表头中并返回它
        jedis.rpoplpush("users", "users2");
        //brpoplpush source destination timeout  //同rpoplpush，但如果源列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        jedis.brpoplpush("users", "users2", 5);


        //llen users
        jedis.llen("users");

        //lindex key index   //索引从0开始！
        jedis.lindex("users", 0);

        //index key start stop   //获取指定范围的元素
        jedis.lrange("users", 2, 10);

        //linsert key BEFORE|AFTER pivot value   //在第一次找到pivot的前或后插入指定元素，如果没找到pivot不做操作
        // linsert users BEFORE u3 u10
        jedis.linsert("users", BinaryClient.LIST_POSITION.BEFORE, "u3", "u10");

        //ltrim key start stop  //删除范围外的元素，仅保留[start,stop]范围内的元素。范围是闭区间！
        jedis.ltrim("users", 3, 6);

        //lrem key count value   //删除最多count个值为value的元素; count大于0从表头搜索，小于0从表尾搜索，等于0全部删除
        jedis.lrem("users", -1, "u3");

    }

    /**
     * set的基本操作
     */
    private static void aboutSet(Jedis jedis) {
        //sadd key member [member ...]  //添加一个或者多个元素到集合(set)里
        jedis.sadd("set1", "e1", "e2","e3","e4","e5");
        jedis.sadd("set2", "s1", "s2","s3","s4","s5");
        //scard key //获取集合里面的元素数量
        jedis.scard("set1");

        //smembers key //获取集合里面的所有元素
        jedis.smembers("set1").forEach(e -> System.out.println(e));

        //sismember key member //给定的元素是否该集合中的成员
        System.out.println(jedis.sismember("set1", "e1"));

        //spop key [count]     //删除并获取一个集合里面的元素
        jedis.spop("set1");

        //srem key member [member ...]  //从集合里删除一个或多个元素
        jedis.srem("set1", "e1", "s0");

        //sdiff key [key ...] //返回给定集合的差集
        jedis.sdiff("set1", "set2");

        //sdiffstore destination key [key ...] //回给定集合的差集,存在一个新的集合中，新集合如果存在会被覆盖
        jedis.sdiffstore("set3", "set1", "set2");

        //sinter key [key ...] //返回给定集合的交集
        jedis.sinter("set1", "set2");

        //sinterstore destination key [key ...] //获得两个集合的交集，并存储在一个关键的结果集
        jedis.sinterstore("set4", "set1", "set2");


        //sunion key [key ...] //返回给定集合的并集
        jedis.sunion("set1", "set2");
        //sunionstore destination key [key ...] //合并set元素，并将结果存入新的set里面
        jedis.sunionstore("set1", "set3");

        //smove source destination member //移动集合里面的一个key到另一个集合
        jedis.smove("set1","set2","e5");

        //srandmember key [count] //从集合里面随机获取一个key
        jedis.srandmember("set1",5);

        //sscan key cursor [MATCH pattern] [COUNT count] //迭代set里面的元素
        ScanResult<String> scan ;
        String cursor="0";
        do {

            scan = jedis.sscan("set2",cursor);
            scan.getResult().forEach(key -> System.out.println(key));
            cursor = scan.getStringCursor();
        }while(!cursor.equals("0"));
    }

    /**
     * sorted set的基本操作
     */
    public static void aboutZset(Jedis jedis) {
        //ZADD key [NX|XX] [CH] [INCR] score member [score member ...] 向有序集合添加一个或多个成员，或者更新已存在成员的分数
         jedis.zadd("zset1", 0.1, "ze1");
        Map<String, Double> membs = new HashMap<>();
        membs.put("ze2",0.2);
        membs.put("ze3",0.2);
        membs.put("ze4",0.3);
        jedis.zadd("zset1",membs);

        //zcard key 获取有序集合的成员数
        jedis.zcard("zset1");

        //zcount key min max 计算在有序集合中指定分数区间的成员数
        jedis.zcount("zset1",0.1,0.2);

        //zlexcount key min max //计算在有序集合指定字典区间内成员数量
        //字典顺序在[ze0,ze5)之前的元素个数
        jedis.zlexcount("zet1","[ze0","(ze5");

        //zincrby key increment member  返回有序集拿中成员的分数值
        jedis.zscore("zset1","ze2");

        //zincrby zset1 2.0 ze1   //有序集合中对指定成员的分数加上增量 increment
        jedis.zincrby("zset1",1.5,"ze1");

        //zrank key member // 返回有序集合中指定成员的索引，有序集成员按分数值递减(从小到大)排序
        jedis.zrank("zset1","ze5");

        //zrevrank key member // 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
        jedis.zrevrank("zset1","ze2");


        //zrange  key start stop  [withscores]//根据索引区间返回指定区间内的成员,可选参数WITHSCORES会返回元素和其分数，而不只是元素
        //start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
        //负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。

        jedis.zrange("zset1",0,5);
        //zrangebylex key min max     //根据字典区间返回有序集合的成员，"(","["
        jedis.zrangeByLex("zset1","[z","[zz");

        //zrangebyscore key min max //根据分数返回有序集合指定区间内的成员   //低到高
        jedis.zrangeByScore("zset1",0,5);
        //zrevrangebyscore key min max //根据分数返回有序集合指定区间内的成员  //高到低
        jedis.zrevrangeByScore("zset1",5,0);

        //zrem key member [member ...] 移除有序集合中的一个或多个成员
        jedis.zrem("zset1","ze5");

        //zremrangebylex key min max 移除有序集合中给定的字典区间的所有成员
        jedis.zremrangeByLex("zset1","[z","[zz");

        //zremrangebyrank key start stop 移除有序集合中给定的排名区间的所有成员
        jedis.zremrangeByRank("zset1",0,5);

        //zremrangebyscore key min max 移除有序集合中给定的分数区间的所有成员
          jedis.zremrangeByScore("zset1",0.2,1.5) ;

        //zinterstore destination numkeys key [key ...]
        //zinterstore zset3 2 zset1 zset2   //计算交集并存到一个新的有序集合中，新集合中元素的分数为此元素在各个原集合中分数之和
        jedis.zinterstore("zset3","zset1","zset2");    //todo

        //ZUNIONSTORE destination numkeys key [key ...] [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]  //todo
        //zunionstore zset9 2 zset1 zset8  //zset1、zset8的并集存入zset9,分数相加
        jedis.zunionstore("zset9","zset1","zset8");
        //zscan zset1 0
        //jedis.zscan()

    }

    /***
     * HyperLogLog
     */
    /***
     * pubsub
     *
     *
     *
     * // PUBSUB CHANNELS
     * //PSUBSCRIBE pattern [pattern ...] Psubscribe 命令订阅一个或多个符合给定模式的频道
     */
    public static void aboutSub(Jedis jedis){
        //subscribe channel
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                System.out.println(message);
            }
        },"chan1");
    }


    public static void aboutPipeline(Jedis jedis){
        //MNOTE：批量提交，减少网络交互次数
        Pipeline pl = jedis.pipelined();
        pl.set("tname","ai");
        pl.append("tname", ".com.cn");
        pl.set("tcount","1");
        pl.incrBy("tcount", 10);

        Response<String> tname = pl.get("tname");
        Response<String> tcount = pl.get("tcount");

        //System.out.println(tcount.get().get());
        //System.out.println(pl.get("tname").get());
        //pipeline中也可以调事务
        // pl.multi()

        System.out.println("-----0------");
        pl.sync();
        System.out.println("-----1------");
        System.out.println(tname.get());
        System.out.println(tcount.get());
       // pl.syncAndReturnAll().forEach(x-> System.out.println(x));

    }

    public static void aboutTrans(Jedis jedis){

            Transaction tx = jedis.multi();  //事务开始
            for (int i = 0; i < 100000; i++) {
                tx.set("t" + i, "t" + i);
                 //在客户端中查看
            }
            List<Object> res = tx.exec();   //提交执行

    }

    public static void aboutPub(Jedis jedis){
        for (int i = 0 ; i<10; i++){
            //publish channel message
            jedis.publish("chan1","hello jedis" + i);
            try {
                Thread.sleep(10000);
                System.out.println(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public static void main(String[] args) throws UnsupportedEncodingException {

//    一致性hash实现的分布式
//        JedisShardInfo host1 =  new JedisShardInfo("192.168.99.130",6379);
//        JedisShardInfo host2 =  new JedisShardInfo("192.168.99.131",6379);
//        host1.setPassword("redispass");
//        host2.setPassword("redispass");
//        List<JedisShardInfo> shards = Arrays.asList(host1,host2);
//
//       ShardedJedis jedis = new ShardedJedis(shards);
//
//        long start = System.currentTimeMillis();
//        for(int i = 0 ; i<=100000; i++){
//            jedis.set("sn"+i,"value-" +i);
//        }
//        System.out.println(System.currentTimeMillis() - start);

        //普通客户端
        Jedis jedis = new Jedis("192.168.99.130");
        jedis.auth("redispass");

        //jedis.pipelined();
        //ping
       // System.out.println(jedis.ping());

        //client setname clientname 命令设置的服务名称, **有什么用呢？**
        //jedis.clientSetname("java client");

//        Optional<String> name = Optional.of(jedis.get("name"));
//        if(name.get().equals("migle")){
//            System.out.println("get name ");
//        };
        /**
         *  hmset
         */
        //jedis.hmset()

/*****************about KEY*********************/
//        //exists key
//        jedis.exists("name");
//        //expire key 5    //设定key的过期时间，单位为秒
//        jedis.expire("name",5);
//        //EXPIREAT key unixTime   //设定key在unixTime指定的时候后过期
//        jedis.expireAt("name",1468205304L);
//
//        //del key
//        jedis.del("name");
//
//        //key pattern
//        //查找所有符合给定模式( pattern)的key
//        jedis.keys("n*");

        //scan 命令及其相关的 sscan, hscan 和 zscan 命令都用于增量迭代一个集合元素
        //http://www.redis.cn/commands/scan.html
       //这四个命令都支持增量式迭代，它们每次执行都只会返回少量元素，所以这些命令可以用于生产环境，而不会出现像 KEYS 或者 SMEMBERS 命令带来的可能会阻塞服务器的问题
        // cursor 设为0时表示开始新一次的迭代，返回0时代表迭代结束
        //返回值都是一个两个元素的数据，第一个元素是为0时代表迭代结束，第二个元素是结果数据组
//        ScanResult<String> scan ;
//        String cursor="0";
//        do {

//            scan = jedis.scan(cursor);
//            scan.getResult().forEach(key -> System.out.println(key));
//            cursor = scan.getStringCursor();
//        }while(!cursor.equals("0"));

//        //persist key
//        // 移除在key上的过期时间限制
//        jedis.persist("tt");
//
//        //type key
//        //返回key的类型:none (key不存在)/string/list/set/zset/hash
//        jedis.type("tt");

//        //rename key newkey   //如果newkey是已存在，会被覆盖
//        jedis.rename("tt1", "tt2");
//
//        //rename key newkey   //如果newkey是不存在时才执行rename
//        jedis.renamenx("tt1", "tt2");
//
//        //pttl key  //以毫秒为单位返回key的剩余的过期时间。
//        jedis.pttl("tt2");
//
//        //pttl key  //以秒为单位返回key的剩余的过期时间。
//        jedis.ttl("tt2");
//
//        //random key //从当前数据库中随机返回一个key
//        jedis.randomKey();
//        //dump key //返回被序列化后的key值;
//        jedis.dump("key");

        //move key 1 //将当前数据库的key移动到指定的数据库当中
        //select 用来选择数据库，redis默认为0
        // jedis.move("key",1);
        //info   //redis服务器的统计信息
        //System.out.println(jedis.info());
        //aboutString(jedis);
        //aboutHash(jedis);
        //aboutList(jedis);
        //aboutSet(jedis);
        //aboutZset(jedis);
        //aboutSub(jedis);
        //aboutPub(jedis);
        // aboutPipeline(jedis);
        //aboutTrans(jedis);
//        Pipeline pl = jedis.pipelined();
//        long start = System.currentTimeMillis();
//        for(int i = 0 ; i<1000000; i++){
//            pl.set("sn"+i,"value-" +i);
//        }
//        pl.sync();
//        System.out.println(System.currentTimeMillis() - start);

        jedis.disconnect();

    }
}
