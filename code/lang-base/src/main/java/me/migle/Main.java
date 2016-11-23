package me.migle;

import me.migle.java8.Command;

/**
 * Created by migle on 2016/7/19.
 */
public class Main {
    public static void main(String[] args) {
        Command c = new TestInter();
        System.out.println(c.append("xxx"));
        System.out.println(c.sayHello("xxx"));
    }
}

class TestInter implements Command{

    @Override
    public String sayHello(String name) {
        return pre +  append(name);
    }
}