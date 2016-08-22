package com.asiainfo.rule;

import java.util.*;

/**
 * Created by migle on 2016/8/15.
 */
public class Exp {

    //先满足现有需求，后需要完善
    //exp = var op exp
    //exp = value or (exp and|or exp)
    //var op value 和  a > v and 的样式，更复杂的后续再实现，和token用空格区分，省的再做拆分
    private String field;
    private String op;    //eq\lt\gt\ge\le\in\nin\and\or
    private String value;

    public Exp(String field, String op, String value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }

    //public com.asiainfo.rule.Exp(String exp){};  //解析出表达式
    //递归
    public boolean compute(Map<String, String> data) {
        if(field ==null||field.isEmpty()){
            System.out.println("表达式为空");
            return true;
        }

        switch (this.op) {
            //TODO： 将出取为的值做验证是不是相应类型？
            case "=":
                ///System.out.println("eq:" + (Integer.valueOf(data.get(this.field)) == Integer.valueOf(value)));
                ///return Integer.valueOf(data.get(this.field)) == Integer.valueOf(value);
                return data.get(this.field).equals(value);
            case "<":
                System.out.println("lt:" + (Integer.valueOf(data.get(this.field)) < Integer.valueOf(value)));
                return Integer.valueOf(data.get(this.field)) < Integer.valueOf(value);
            case "<=":
                System.out.println("le:" + (Integer.valueOf(data.get(this.field)) <= Integer.valueOf(value)));
                return Integer.valueOf(data.get(this.field)) <= Integer.valueOf(value);
            case ">":
                System.out.println("gt:" + (Integer.valueOf(data.get(this.field)) > Integer.valueOf(value)));
                return Integer.valueOf(data.get(this.field)) > Integer.valueOf(value);
            case ">=":
                System.out.println("ge:" + (Integer.valueOf(data.get(this.field)) >= Integer.valueOf(value)));
                return Integer.valueOf(data.get(this.field)) >= Integer.valueOf(value);

            case "range":  //[s,e],(s,e],[s,e),(s,e)   //TODO:现有需求中有[]
                String s[] = value.split(",");
                return Integer.valueOf(data.get(this.field)) >= Integer.valueOf(s[0]) &&
                        Integer.valueOf(data.get(this.field)) <= Integer.valueOf(s[1]);

            case "in": //
                Set<String> vs = new HashSet<>(Arrays.asList(value.split(",")));
                return vs.contains(data.get(this.field));

            default:
                //不认识的时候也返回true
                return true;
        }
    }

    public static void main(String[] args) {
//        Map<String,String> data = new HashMap<>();
//        data.put("x","ee");
//        com.asiainfo.rule.Exp exp = new com.asiainfo.rule.Exp("x","in","aa,bb,cc,dd");

        Map<String,String> data = new HashMap<>();
        data.put("x","50");
        Exp exp = new Exp("x","range","50,100");


        System.out.println(exp.compute(data));
    }
}
