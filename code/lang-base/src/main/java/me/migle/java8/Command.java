/**
 * Created by migle on 2016/7/19.
 */
package me.migle.java8;

public interface Command {
    String pre = " hi ";
    default String append(String str){
        return str+"!;";
    }

    String sayHello(String name);
}
