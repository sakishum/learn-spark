package me.migle.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author migle on 2017/8/24.
 */
public class ThreadPoolTest {

    public static void main(String[] args) {
//        Thread t = new Thread(() -> {
//            try {
//                System.out.println(123);
//                TimeUnit.SECONDS.sleep(5);
//                System.out.println(456);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//
//        Executor executor = Executors.newCachedThreadPool();
//        executor.execute(t);
//        System.out.println("11");
        AtomicInteger i = new AtomicInteger(10);
        System.out.println(i.compareAndSet(10,11));
        System.out.println(i.compareAndSet(10,16));
        System.out.println(i.get());

    }
}
