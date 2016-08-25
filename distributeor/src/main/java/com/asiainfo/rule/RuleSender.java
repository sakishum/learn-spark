package com.asiainfo.rule;

import com.asiainfo.Conf;
import com.asiainfo.util.DbUtil;
import com.asiainfo.util.ResultMapper;
import redis.clients.jedis.Jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by migle on 2016/8/16.
 * <p/>
 * 读取数据库中的规则，检查正确性及时间，发送至redis中供数据处理程序读取,清理缓存中的过期规则
 */
public class RuleSender {

    private String url = Conf.db_url;
    private String user = Conf.db_user;
    private String pwd = Conf.db_pwd;

    private String redis_host = Conf.redis_host;
    private String redis_pwd = Conf.redis_pwd;
    private String redis_rule_key = Conf.redis_rule_key;


    private String ruleTableName = "rule_data_transfer";
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //只获取状态为1的,且时间在生效范围内容的规则加载至redis
    public void cacheRuler() throws Exception {
        DbUtil db = new DbUtil(url, user, pwd);
        String curDate = sf.format(new Date());
        String sql = String.format("select * from  %s  where state='1'  and start_time<= '%s'  and end_time > '%s' ", ruleTableName, curDate, curDate);
        System.out.println(sql);
        List<String> rules = db.query(sql, new RuleResultMapper());
        Jedis jedis = new Jedis(redis_host);
        jedis.auth(redis_pwd);
        //写入redis，规则应该不多，一条一条写入也没关系
        //当前生效的规则全部重新写入一次
        rules.stream().forEach(str -> {
            Rule r = new Rule(str);
            if (r.validate()) {
                //jedis.sadd(redis_rule_key, str);
                jedis.hset(redis_rule_key, r.getRuleid(), str);
            }
        });

        //清理下线规则
        if(rules.size() != jedis.hlen(redis_rule_key)){
            String offlinesql = String.format("select * from  %s  where state != '1'",ruleTableName);
            System.out.println(offlinesql);
            List<String> offline = db.query(offlinesql, new RuleResultMapper());
            offline.forEach(str->{
                Rule r = new Rule(str);
               // System.out.println(r);
                //System.out.println(jedis.sismember(redis_rule_key,r));
               // jedis.srem(redis_rule_key,r);
                jedis.hdel(redis_rule_key,r.getRuleid());
            });
        }
        jedis.close();
        db.close();
    }

    //判断状态为0的规则是不是正确的规则，是则改状态为1，不是则将状态改为-1
    public void receiveRuel() throws Exception {
        //update rule_data_transfer set status='-1' where rule_id in();
        DbUtil db = new DbUtil(url, user, pwd);
        String curDate = sf.format(new Date());
        String sql = String.format("select * from  %s  where state='0'  and start_time<= '%s'  and end_time > '%s' ", ruleTableName, curDate, curDate);  //FIXME:时间段控制
        System.out.println(sql);
        List<String> query = db.query(sql,new RuleResultMapper());

        query.stream().forEach(str -> {
            Rule r = new Rule(str);

            //FIXME:不在时间段内的是不是单独标注一个状态？
            if (r.validate()) {
                try {
                    db.update(String.format("update  %s set state='1'  where rule_id='%s'", ruleTableName, r.getRuleid()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("!!!!!!!!!!!!!!!!更新则状态出错，请查看" + r.getRuleid());
                }
            } else {
                try {
                    db.update(String.format("update  %s set state='-1'  where rule_id='%s'", ruleTableName, r.getRuleid()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("!!!!!!!!!!!!!!!!更新则状态出错，请查看" + r.getRuleid());
                }
            }
        });

        db.close();
    }


    //定期从redis里面清理过期及下线的规则
    public void cleanRedis() throws SQLException {
        Jedis jedis = new Jedis(redis_host);
        jedis.auth(redis_pwd);
        //清理过期规则
        jedis.hgetAll(redis_rule_key).forEach((k,v) -> {
            Rule r = new Rule(v);
            if (r.getEndtime().getTime() <= System.currentTimeMillis()) {
                jedis.hdel(redis_rule_key,r.getRuleid());
            }
        });
        jedis.close();
    }

    class RuleResultMapper implements ResultMapper<String>{
        @Override
        public String map(ResultSet rs) throws SQLException {
            StringBuilder ss = new StringBuilder("{");
            ss.append(String.format("\"ruleid\":\"%s\",", rs.getString("rule_id")));
            ss.append(String.format("\"eventid\":\"%s\",", rs.getString("event_id")));
            if (rs.getString("fields") != null && !rs.getString("fields").isEmpty()) {
                ss.append(String.format("\"fields\":\"%s\",", rs.getString("fields")));
            }
            if (rs.getString("group_key") != null && !rs.getString("group_key").isEmpty()) {
                ss.append(String.format("\"groupkey\":\"%s\",", rs.getString("group_key")));
            }
            ss.append(String.format("\"starttime\":\"%s\",", sf.format(rs.getTimestamp("start_time"))));
            ss.append(String.format("\"end_time\":\"%s\"", sf.format(rs.getTimestamp("end_time"))));

//                Map<String,String> m = new HashMap<String,String>();
//                m.put("ruleid",rs.getString("rule_id"));
//                m.put("eventid",rs.getString("event_id"));
//                m.put("fields",rs.getString("fields"));
//                m.put("groupkey",rs.getString("group_key"));
//                m.put("starttime",rs.getString("start_time"));
//                m.put("endtime",rs.getString("end_time"));
//                return m;
            ss.append("}");
            return ss.toString();
        }
    }

    public static void main(String[] args) throws Exception {
//        RuleSender r = new RuleSender();
//        //启动三个任务，接收、送缓存，删除过期
//        //r.receiveRuel();   //检查规则，更改状态
//        r.cacheRuler();     //送缓存
//        // r.cleanRedis();

while(true){
    RuleSender r = new RuleSender();
    System.out.println("------------------receiveRuel----------------");
    r.receiveRuel();
    System.out.println("------------------cacheRuler----------------");
    r.cacheRuler();
    System.out.println("------------------cleanRedis----------------");
    r.cleanRedis();
    Thread.sleep(1000*60*5);
}


    }
}


/*********
 * create table rule_data_transfer(
 * rule_id       varchar(30),
 * event_id      varchar(30),
 * fields        varchar(100),
 * group_key     varchar(100),
 * start_time    timestamp,
 * end_time      timestamp,
 * state         varchar(2)
 * );
 * <p/>
 * insert into rule_data_transfer values
 * ('qcd_123','event_netpay','payment_fee range 10,50','guser1','2016-08-15 10:49:27','2016-09-15 14:49:27','0')
 * <p/>
 * insert into rule_data_transfer values
 * ('qcd_456','event_netpay','payment_fee range 100,200','guser1','2016-08-15 10:49:27','2016-09-15 14:49:27','0')
 * <p/>
 * <p/>
 * insert into rule_data_transfer values
 * ('qcd_789','event_netpay','payment_fee range 100,200','guser1','2016-08-15 10:49:27','2016-08-15 14:49:27','0')
 * insert into rule_data_transfer values
 * ('qcd_200','event_busi_order','prod_prcid in m001,m002','guser1','2016-08-15 10:49:27','2016-09-15 15:00:00','0')
 *******/