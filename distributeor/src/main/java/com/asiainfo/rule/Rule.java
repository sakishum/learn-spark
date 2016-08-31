package com.asiainfo.rule;

import com.alibaba.fastjson.JSON;
import com.asiainfo.Conf;
import com.asiainfo.common.RedisClusterPool;
import java.io.Serializable;
import java.util.*;

/**
 * Created by migle on 2016/8/12.
 * 解析、判断规则
 */
public class Rule implements Serializable {
    //TODO:应该由过滤条件和输出组成

    private String ruleid;     //规则id  必选
    private String eventid;     //事件id, 必选
    private String fields;  //字段条件，与固定估比较,>\<\>=\<=\and


    private String groupkey;  //群组过滤时的redis key 可选
    private Date starttime;  //开始时间
    private Date endtime;    //结束时间

    private Exp exp;           //表达式
    private String consumeTopic;   //源kafka topic，系统判断

    private final static Set<String> OP = new HashSet<>(Arrays.asList(new String[]{"=", ">", ">=", "<", "<=", "range", "in"}));

    //{ruleid,eventid,exp,groupkey,starttime,endtime}
    //intopic、outtopic：源topic系统指定，输出topic暂时不开放

    public Rule(String rule) {
        parse(rule);
    }

    private Rule() {
    }

    public boolean validate() {
        if (isEmpty(ruleid) || isEmpty(eventid) || starttime == null || endtime == null) {
            return false;
        }
        //表达式判断
        if (!isEmpty(fields)) {
            String[] e = fields.split(" ");
            if (e.length != 3) {
                return false;
            }
            if (!OP.contains(e[1])) {
                return false;
            }
            //range只能用于数字  number range min,max
            if (e[1].equals("range") && !e[2].matches("^\\d+,\\d+$")) {
                return false;
            }

            //in 列表取值不验证了
        }

        //groupkey判断

        return true;
    }

    public boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    private void parse(String rule) {
        if (rule == null || rule.isEmpty()) {
            System.out.println("check your rule !!!!");
        }

        this.consumeTopic = "topic-1";     //根据系统配置判断
        //OutPut!

        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        Rule r = JSON.parseObject(rule, Rule.class);

        this.ruleid = r.getRuleid();
        this.eventid = r.getEventid();
        this.fields = r.getFields();
        this.groupkey = r.getGroupkey();
        this.starttime = r.getStarttime();
        this.endtime = r.getEndtime();

        //TODO 规则验证!
        if (validate()) {
            //var >=|<=|>|=|< value
            //var in v1,v2,v3
            //var range 50,100
            String[] e = this.fields.split(" ");
            this.exp = new Exp(e[0], e[1], e[2]);
        } else {
            //FIXME
            System.out.println("规则错误");
        }
    }

    public Output rule(Map<String, String> data) {
        //todo:字段选择也是规则的一部分
        //是否在要求的时间段内
        if (System.currentTimeMillis() < this.starttime.getTime() || System.currentTimeMillis() > this.endtime.getTime()) {
            //时间未到或已结束
            return new Output();
        }
        if (this.groupkey == null ? this.exp.compute(data) :
                //FIXME:暂时群组判断就取"phone_no"字段
                this.exp.compute(data) && IsInRedis(data.getOrDefault("phone_no",""),this.ruleid)) {
            Map<String, String> m = new HashMap<>(data);
            m.put("ruleid", this.ruleid);
            m.put("eventid", this.eventid);
            //TODO:后续根据规则中的配置输出
            Output out = new Output(Conf.eventToTopic.get(m.getOrDefault("eventid","")), m);
            return out;
        } else {
            return new Output();
        }

    }

//TODO:要把这一部分合并成Exp的一部分  filed:redis#redis_key
    private boolean IsInRedis(String str,String ruldid) {

        //Jedis jedis = ReidsPool.pool().getResource();
        //FIXME:找阿杜确认key
        return RedisClusterPool.pool().sismember("ACTIVITYLIST."+ruldid+".d01", str);

    }


    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getGroupkey() {
        return groupkey;
    }

    public void setGroupkey(String groupkey) {
        this.groupkey = groupkey;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public static void main(String[] args) {
        //    Rule r = new Rule("payment_fee eq 10");
//        //System.out.println(r.rule(data));
//        System.out.println(r.IsInRedis("13358628685", jedis));

//        String rule1="{\"ruleid\":\"123\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee >= 10\"," +
//                "\"groupkey\":\"guser1\",\"starttime\":\"2016-08-15 10:49:27\",\"endtime\":\"2016-09-15 14:49:27\" }";

//        String rule1="{\"ruleid\":\"123\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 10,100\"," +
//                "\"groupkey\":\"guser1\",\"starttime\":\"2016-08-15 10:49:27\",\"endtime\":\"2016-09-15 14:49:27\" }";

//        System.out.println(rule1);
//        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//        Rule r = JSON.parseObject(rule1,Rule.class);
//        System.out.println(r.getRuleid());
//        System.out.println(r.getEventid());
//        System.out.println(r.getFields());
//        System.out.println(r.getGroupkey());
//        System.out.println(r.getStarttime());
//        System.out.println(r.getEndtime());

//        Map<String, String> data = new HashMap<>();
//        data.put("phone_no", "18797384480");
//        data.put("payment_fee", "30");
//        data.put("login_no", "m001");
//        data.put("date", "2016-08-01");
//
//        Rule r = new Rule(rule1);
//        System.out.println(r.getRuleid());
//        System.out.println(r.getEventid());
//        System.out.println(r.getFields());
//        System.out.println(r.getGroupkey());
//        System.out.println(r.getStarttime());
//        System.out.println(r.getEndtime());
//
//        Jedis jedis = new Jedis("192.168.99.130");
//        jedis.auth("redispass");
//
//        System.out.println(r.rule(data, jedis));


        // String val = "50,100";
        //System.out.println(val.matches("^\\d+,\\d+$"));;
//        Rule r = new Rule(rule1);
//        System.out.println(r.validate());


        String[] rules = new String[]{
                "{\"ruleid\":\"qcd_456\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 100,200\",\"starttime\":\"2016-08-16 18:16:55\",\"end_time\":\"2016-09-15 14:49:27\"}",
                "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}",
                "{\"ruleid\":\"qcd_456\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 100,200\",\"starttime\":\"2016-08-16 18:16:55\",\"end_time\":\"2016-09-15 14:49:27\"}",
                "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}",
                "{\"ruleid\":\"qcd_456\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 100,200\",\"starttime\":\"2016-08-16 18:16:55\",\"end_time\":\"2016-09-15 14:49:27\"}",
                "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}",
                "{\"ruleid\":\"qcd_456\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 100,200\",\"starttime\":\"2016-08-16 18:16:55\",\"end_time\":\"2016-09-15 14:49:27\"}",
                "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}",
                "{\"ruleid\":\"qcd_456\",\"eventid\":\"event_netpay\",\"fields\":\"payment_fee range 100,200\",\"starttime\":\"2016-08-16 18:16:55\",\"end_time\":\"2016-09-15 14:49:27\"}",
                "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}"
        };

        String str = "{\"ruleid\":\"qcd_200\",\"eventid\":\"event_busi_order\",\"fields\":\"prod_prcid in m001,m002\",\"groupkey\":\"guser1\",\"starttime\":\"2016-08-17 18:00:21\",\"end_time\":\"2016-09-15 15:00:00\"}";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            Rule r = new Rule(str);
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleid='" + ruleid + '\'' +
                ", eventid='" + eventid + '\'' +
                ", fields='" + fields + '\'' +
                ", groupkey='" + groupkey + '\'' +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                '}';
    }
}
