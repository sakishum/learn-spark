import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by migle on 2016/8/12.
 */
public class Rule implements Serializable{
    //TODO:应该由过滤条件和输出组成
    private String ruleid;
    private String evetid;
    private Exp exp;
    private String groupkey;

    private String topic;


    public Rule(String rule) {
        this.exp = parse(rule);
    }

    public Rule(String rule,String groupkey) {
        this.exp = parse(rule);
        this.groupkey = groupkey;
    }

    private Exp parse(String rule){
        this.ruleid=rule;        //TODO
        this.topic="topic-p-1";  //TODO

        //
        if(rule==null || rule.isEmpty() ){
            System.out.println("check your rule !!!!");
            System.exit(-1);
        }

        String[] s = rule.split(" ");
        return new Exp(s[1],Integer.valueOf(s[2]));
    }

    public Output  rule(Map<String,String> data,Jedis jedis){
        //todo:字段选择也是规则的一部分
        //return groupkey==null?this.exp.compute(Integer.valueOf(data.get("payment_fee"))):
        //        this.exp.compute(Integer.valueOf(data.get("payment_fee")))&& exists(data.get("phone_no"),jedis);

        if(groupkey==null?this.exp.compute(Integer.valueOf(data.get("payment_fee"))):
                this.exp.compute(Integer.valueOf(data.get("payment_fee")))&& exists(data.get("phone_no"),jedis)){
            Map<String,String> m = new HashMap<>(data);
            m.put("ruleid",ruleid);
            //TODO
            String msg = "{ruldid:"+ruleid + " phone_no:" + data.get("phone_no") +"payment_fee:"+data.get("payment_fee")+"}";
            System.out.println("msg=======" + msg);
            System.out.println("topic====="+this.topic);

            Output out = new Output(this.topic,msg);
            return out;
        }else{
            return null;
        }

    }

    public Map<String,String>  rule(Map<String,String> data){
        //todo:字段选择也是规则的一部分
        return  this.exp.compute(Integer.valueOf(data.get("payment_fee"))) ? data:null;
    }



    public void output(){

    }

    private boolean exists(String str,Jedis jedis){
        return jedis.sismember(groupkey,str);
    }

    class Exp{
        String op;  //eq\lt\gt\ge\le\in\nin\and\or
        Integer value;

        public Exp( String op, Integer value) {

            this.op = op.toLowerCase();
            this.value = value;
        }

        public boolean compute(int var){
            switch (this.op){
                case "eq" :
                    System.out.println("eq:"+(var==value)); return  var==value;
                case "lt" : return  var<value;
                case "le" :
                    System.out.println("le:" + (var<=value));return  var<=value;
                case "gt" :
                    System.out.println("gt"+(var>value));return  var>value;
                case "ge" :
                    System.out.println("ge"+(var>=value));return  var>=value;
                default:return false;
            }
        }
    }

    public static void main(String[] args) {
        Rule r = new Rule("payment_fee eq 10","guser1");
        Map<String,String> data = new HashMap<>();
        data.put("phone_no","18797384480");
        data.put("payment_fee","10");
        data.put("login_no","m001");
        data.put("date","2016-08-01");

        Jedis jedis = new Jedis("192.168.99.130");
                jedis.auth("redispass");

        //System.out.println(r.rule(data));
        System.out.println(r.exists("13358628685",jedis));
    }
}
