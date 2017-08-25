package me.migle.concurrent;

import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author migle on 2017/8/18.
 */
public class T2 {

    public static void main(String[] args) {
        ExecutorService e = Executors.newCachedThreadPool();
        H1 h1 = new H1();
        for (int i = 0; i < 5; i++) {
            e.execute(new Thread2(h1));
        }
        e.shutdown();
    }
}
class H1{
    public static AtomicInteger ai = new AtomicInteger(10);

    synchronized  public void h(){
        System.out.println(Thread.currentThread().getName() + " | " + LocalTime.now().toString());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+ " | " + LocalTime.now().toString());

    }
}
class Thread2 extends Thread{
    private H1 h;

    public Thread2(H1 h) {
        this.h = h;
    }

    @Override
    public void run() {
        this.h.h();
        System.out.println(H1.ai.decrementAndGet());
    }
}
