import com.asiainfo.Conf;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * Created by migle on 16/8/14.
 */
public class Main {
//    public static void testPool(){
//        JedisPool jp = new JedisPool(new GenericObjectPoolConfig(), Conf.redis_host,Conf.redis_port,2000,Conf.redis_pwd);
//        System.out.println(jp.toString());
//    }
//    public static void main(String[] args) {
//            JedisPool jp = new JedisPool(new GenericObjectPoolConfig(), Conf.redis_host,Conf.redis_port,2000,Conf.redis_pwd);
//        System.out.println(jp.toString());
//        testPool();
//    }

    public static void main(String[] args) {
        System.out.println(Conf.db_driver);
    }
}
