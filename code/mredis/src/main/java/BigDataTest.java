import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * Created by migle on 2016/9/9.
 */
public class BigDataTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
//        for (int i = 0; i < 10000000; i++) {
//            jedis.set("key-" + i, "" + new Random().nextInt(100));
//        }
        System.out.println("===============");
        for (int i = 0; i < 100000; i++) {
           // jedis.set("key-" + i, ""+new Random().nextInt(100));
            jedis.incrBy("key-" + new Random().nextInt(100), i);
        }



        jedis.close();
    }
}
