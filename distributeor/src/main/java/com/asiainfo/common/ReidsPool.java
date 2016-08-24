package com.asiainfo.common;

import com.asiainfo.Conf;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import java.io.Serializable;

/**
 * Redis连接池
 * Created by migle on 2016/8/19.
 */
public class ReidsPool{
    private ReidsPool() {
    }
//private volatile static JedisPool pool;
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
//TODO:连接池参数设置

    public static final JedisPool pool(){
        return PoolHolder.pool;
    }

    public static final void getInfo(){
        System.out.println("=========================redispool================================");
        System.out.println("NumActive:"+PoolHolder.pool.getNumActive());
        System.out.println("NumIdle:"+PoolHolder.pool.getNumIdle());
        System.out.println("NumWaiters:"+PoolHolder.pool.getNumWaiters());
        System.out.println("=========================redispool================================");
    }
    private static class PoolHolder{
        public final static JedisPool pool = new JedisPool(new GenericObjectPoolConfig(), Conf.redis_host, Conf.redis_port, 2000, Conf.redis_pwd);
        static{
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                synchronized  public void run() {
                    if (pool != null) {
                        pool.destroy();
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        ReidsPool.pool();
        ReidsPool.pool();
        ReidsPool.pool();
    }
}
