package me.migle;

/**
 * Created by migle on 2017/4/27.
 */
public class AboutThread {
    public static void main(String[] args) {
        Test t = new Test();
        t.test();
    }

}

class Test{
    public void test(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement s : stackTrace){
            System.out.println(s.getClassName());
        }
    }
}

