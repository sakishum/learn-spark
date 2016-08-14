package help;

import redis.clients.jedis.Jedis;

/**
 * Created by migle on 16/8/14.
 */
public class RedisHelp {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.99.130");
        jedis.auth("redispass");
        for (int i = 0; i < 1000; i++) {
            jedis.sadd("guser1",DataGen.getPhoneNo());
        }

    }
}
