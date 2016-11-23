package me.migle.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by migle on 2016/8/19.
 */
public class ReidsPool {
    private volatile static JedisPool pool;

//    static{
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            synchronized  public void run() {
//                if (pool != null) {
//                    ///System.out.println("执行退出函数");
//                    pool.destroy();
//                }
//            }
//        });
//    }

    private ReidsPool() {
    }

//    public static JedisPool pool() {
//        if (pool == null) {
//            synchronized (ReidsPool.class) {
//                if (pool == null) {
//                    pool = new JedisPool(new GenericObjectPoolConfig(), "192.168.99.130", 6379, 2000, "redispass");
//                }
//            }
//        }
//        return pool;
//    }

    public static final JedisPool pool(){
        return PoolHolder.pool;
    }

    private static class PoolHolder{
      public final static JedisPool pool = new JedisPool(new GenericObjectPoolConfig(), "192.168.99.130", 6379, 2000, "redispass");
    }

    public static void main(String[] args) {
        ReidsPool.pool();
        ReidsPool.pool();
    }
}

