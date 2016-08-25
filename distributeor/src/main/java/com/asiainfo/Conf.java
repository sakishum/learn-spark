/**
 * Created by migle on 2016/8/10.
 */
package com.asiainfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Conf {
    private static final Properties param = new Properties();
    static {
        InputStream in = Conf.class.getClassLoader().getResourceAsStream("ai-event.properties");
        try {
            param.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


//    public static final String kafka="vm-centos-00:9092,vm-centos-01:9092";
//    public static final String zkhosts="vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181";
//    public static final String groupid="ai-event";
//    public static final String consume_topic_netpay="topic-2";
//    public static final String consume_topic_usim="topic-1";
//    public static final String consume_topic_order="topic-3";

    public static final String kafka= param.getProperty("kafka.broker");
    public static final String zkhosts=param.getProperty("zk.hosts");
    public static final String groupid=param.getProperty("kafka.groupid");

    public static final String consume_topic_netpay=param.getProperty("source.topic.netpay");
    public static final String consume_topic_usim=param.getProperty("source.topic.usimchange");
    public static final String consume_topic_order=param.getProperty("source.topic.busiorder");




    //public static final String produceTo="topic-p-1";

    public static final String eventNetpay = "event_netpay";
    public static final String eventUSIMChange = "event_usim_change";
    public static final String eventBusiOrder = "event_busi_order";

    public static Map<String,String> eventToTopic = new HashMap<String,String>(){
        {
            this.put(eventNetpay,param.getProperty("output.topic.event.netpay"));
            this.put(eventUSIMChange,param.getProperty("output.topic.event.usimchange"));
            this.put(eventBusiOrder,param.getProperty("output.topic.event.busiorder"));
        }
    };
/**********************************规则相关***************************************/
    //规则存在数据库中
    public static final String db_url= param.getProperty("rule.db.url");
    public static final String db_user=param.getProperty("rule.db.user");
    public static final String db_pwd=param.getProperty("rule.db.pwd");
    public static final String db_driver = param.getProperty("rule.db.driver");

    //规则缓存相关
    public static final String redis_host = param.getProperty("redis.hosts");
    public static final int redis_port = Integer.valueOf(param.getProperty("redis.port"));
    public static final String redis_pwd =  param.getProperty("redis.pwd");
    public static final String redis_rule_key=param.getProperty("redis.rule.key");

}
