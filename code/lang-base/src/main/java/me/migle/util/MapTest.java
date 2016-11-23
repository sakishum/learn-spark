package me.migle.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by migle on 2016/8/2.
 */
public class MapTest {
    public static void main(String[] args) {
        Map<String,String> m = new HashMap<>();
        m.put("a","a1111");
        m.put("c","c1111");
        m.put("b","b1111");

        for(Map.Entry<String,String> ks:m.entrySet()){
            System.out.println(ks.getKey()+":" +ks.getValue());
        }


    }
}
