package com.asiainfo.common;

import com.asiainfo.Conf;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by migle on 2016/8/31.
 */
public class RedisClusterPool {
        private final static GenericObjectPoolConfig poolConfig  = new GenericObjectPoolConfig();
        private final static Set<HostAndPort> nodes  = new HashSet<>();
        private RedisClusterPool() {
        }
        static {

            for (String s : Conf.redis_cluster.split(",")) {
                String[] hp = s.split(":");
                nodes.add(new HostAndPort(hp[0],Integer.valueOf(hp[1])));
            }

            poolConfig.setMaxIdle(5);
            poolConfig.setMaxTotal(20);
        }

        public static final JedisCluster pool(){
            return PoolHolder.pool;
        }
        private static class PoolHolder{
            public final static JedisCluster pool = new JedisCluster(nodes,poolConfig);
            static{
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    synchronized  public void run() {
                        if (pool != null) {
                            try {
                                pool.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

    public static void main(String[] args) {
        for (HostAndPort node : RedisClusterPool.nodes) {
            System.out.println(node);
        }
    }
}
