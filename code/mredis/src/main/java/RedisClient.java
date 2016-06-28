import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by migle on 2016/6/28.
 */
public class RedisClient {
    private final static List<String> REDIS_HOSTS = Arrays.asList("vm-centos-00");

    public static void main(String[] args) {
        Jedis jedis = new Jedis(REDIS_HOSTS.get(0));
        /**
         *   set name migle
         *   get name
         *
         **/
        jedis.set("name", "migle");
        Optional<String> name = Optional.of(jedis.get("name"));
        if(name.get().equals("migle")){
            System.out.println("get name ");
        };
        /**
         *  hmset
         */
        //jedis.hmset()




        jedis.set("name","mmmmm","XX","EX",5L);

    }
}
