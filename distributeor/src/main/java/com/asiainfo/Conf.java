/**
 * Created by migle on 2016/8/10.
 */
package com.asiainfo;

import java.util.HashMap;
import java.util.Map;

public class Conf {
    public static final String kafka="vm-centos-00:9092,vm-centos-01:9092";
    public static final String zkhosts="vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181";
    public static final String groupid="ai-event";


    public static final String consume_topic_netpay="topic-2";
    public static final String consume_topic_usim="topic-1";
    public static final String consume_topic_order="topic-3";

    //public static final String produceTo="topic-p-1";

    public static final String eventNetpay="event_netpay";
    public static final String eventUSIMChange="event_usim_change";
    public static final String eventBusiOrder="event_busi_order";

    public static Map<String,String> eventToTopic = new HashMap<String,String>(){
        {
            this.put(eventNetpay,"qcd_ruselt_netpay_1");
            this.put(eventUSIMChange,"qcd_ruselt_usim_change");
            this.put(eventBusiOrder,"qcd_ruselt_busi_order");
        }
    };
/**********************************规则相关***************************************/
    //规则存在数据库中
    public static final String db_url= "jdbc:mariadb://192.168.99.130:3306/test?useUnicode=true&characterEncoding=utf-8";
    public static final String db_user="root";
    public static final String db_pwd="iammigle";
    public static final String db_driver = "org.mariadb.jdbc.Driver";

    //规则缓存相关
    public static final String redis_host = "192.168.99.130";
    public static final int redis_port = 6379;
    public static final String redis_pwd = "redispass";
    public static final String redis_rule_key="redis_rule_key";

}
