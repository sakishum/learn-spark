package me.migle.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author migle on 2017/8/16.
 */
public class LambdaTest {
    public void test(){
        List<String> tl = new ArrayList<>(Arrays.asList("aaa", "bbb", "cccc"));
        tl.stream().map(String::toUpperCase).filter(x -> {
            System.out.println(this.getClass().getCanonicalName());
            return x.matches(".*[a|A].*");
        })
            .forEach(System.out::println);

    }
    public static void main(String[] args) {
        LambdaTest t = new LambdaTest();
        t.test();
    }
}
