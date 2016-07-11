import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by migle on 2016/6/28.
 */
public class RedisAPI {
    private final static List<String> REDIS_HOSTS = Arrays.asList("vm-centos-00");

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
    }

    /**
     * list的基本操作
     */
    private static void aboutList(Jedis jedis) {
        //lpush key e1  //列表头增加，key不存在时新增key
        //lpushx key e1  //key存在时列表头增加，不存在时不做操作
        jedis.lpush("users", "u1");
        jedis.lpushx("users","u1");

        //rpush key e2  //列表尾增加，key不存在时新增key
        //rpushx key e1  //key存在时列表尾增加，不存在时不做操作
        jedis.rpush("users", "u5");
        jedis.rpushx("users", "u5");


        //lset key index value //通过索引设置列表元素的值
        jedis.lset("users",2,"ux");

        //rpop key  //移除并且返回key对应list的最后一个元素
        //lpop key  //移除并且返回key对应list的第一个元素
        //brpop key1 [key2 ] timeout 移出并获取列表的最后一个元素，如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        //blpop key1 [key2 ] timeout 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        jedis.rpop("users");
        jedis.lpop("users");

        //rpoplpush source destination //从列表中弹出一个值，将弹出的元素插入到另外一个列表头中并返回它
        jedis.rpoplpush("users","users2");
        //brpoplpush source destination timeout  //同rpoplpush，但如果源列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
        jedis.brpoplpush("users","users2",5);


        //llen users
        jedis.llen("users");

        //lindex key index   //索引从0开始！
        jedis.lindex("users", 0);

        //index key start stop   //获取指定范围的元素
        jedis.lrange("users",2,10);

        //linsert key BEFORE|AFTER pivot value   //在第一次找到pivot的前或后插入指定元素，如果没找到pivot不做操作
        // linsert users BEFORE u3 u10
        jedis.linsert("users", BinaryClient.LIST_POSITION.BEFORE,"u3","u10");

        //ltrim key start stop  //删除范围外的元素，仅保留[start,stop]范围内的元素。范围是闭区间！
        jedis.ltrim("users",3,6);

        //lrem key count value   //删除最多count个值为value的元素; count大于0从表头搜索，小于0从表尾搜索，等于0全部删除
        jedis.lrem("users",-1,"u3");

    }

    private static void aboutSet(Jedis jedis){
    }
    public static void main(String[] args) {
        Jedis jedis = new Jedis(REDIS_HOSTS.get(0));

        //ping
        System.out.println(jedis.ping());

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
//
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


        //aboutString(jedis);
        //aboutHash(jedis);
        aboutList(jedis);

    }
}
