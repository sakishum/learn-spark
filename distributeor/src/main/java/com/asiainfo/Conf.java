/**
 * Created by migle on 2016/8/10.
 */
package com.asiainfo;
public class Conf {
    public final static String kafka="vm-centos-00:9092,vm-centos-01:9092";
    public static final String zkhosts="vm-centos-01:2181,vm-centos-02:2181,vm-centos-03:2181";
    public static final String groupid="ai-event";


    public static final String redis_rule_key="redis_rule_key";

    public static final String consumeFrom="topic-hw-1";
    public static final String produceTo="topic-p-1";

    public static final String eventNetpay="event_netpay";
    public static final String eventUSIMChange="event_usim_change";
    public static final String eventBusiOrder="event_busi_order";


}
