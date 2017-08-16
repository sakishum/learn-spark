package me.migle.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by migle on 2016/6/27.
 */
public class AboutSemaphore {

    private static int cnt = 0;

    public static void main(String[] args) {
        //只有一个许可的时候相当于Lock
        //可以用来控线程数量
        //Semaphore可以用于做流量控制，特别公用资源有限的应用场景，比如数据库连接。
        // 假如有一个需求，要读取几万个文件的数据，因为都是IO密集型任务，我们可以启动几十个线程并发的读取，
        // 但是如果读到内存后，还需要存储到数据库中，而数据库的连接数只有10个，
        // 这时我们必须控制只有十个线程同时获取数据库连接保存数据，否则会报错无法获取数据库连接。这个时候，我们就可以使用Semaphore来做流控


        Semaphore semaphore = new Semaphore(1);  //只有一个许可

//CountDownLatch的countDown方法时，N就会减1，CountDownLatch的await会阻塞当前线程，直到N变成零。
//由于countDown方法可以用在任何地方，所以这里说的N个点，
//可以是N个线程，也可以是1个线程里的N个执行步骤。用在多个线程时，你只需要把这个CountDownLatch的引用传递到线程里
//CountDownLatch不可能重新初始化或者修改CountDownLatch对象的内部计数器的值。
// 一个线程调用countDown方法 happen-before 另外一个线程调用await方法
//        CountDownLatch countDownLatch = new CountDownLatch(2);
//        countDownLatch.countDown();
//        countDownLatch.await();

// 它要做的事情是，让一组线程到达一个屏障（也可以叫同步点）时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，
// 所有被屏障拦截的线程才会继续干活。CyclicBarrier默认的构造方法是CyclicBarrier(int parties)，其参数表示屏障拦截的线程数量，
// 每个线程调用await方法告诉CyclicBarrier我已经到达了屏障，然后当前线程被阻塞

//        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
//        cyclicBarrier.await();
//        cyclicBarrier.reset();


// CountDownLatch的计数器只能使用一次。而CyclicBarrier的计数器可以使用reset() 方法重置。
// 所以CyclicBarrier能处理更为复杂的业务场景，比如如果计算发生错误，可以重置计数器，并让线程们重新执行一次。
// CyclicBarrier还提供其他有用的方法，比如getNumberWaiting方法可以获得CyclicBarrier阻塞的线程数量。
// isBroken方法用来知道阻塞的线程是否被中断。比如以下代码执行完之后会返回true

        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int ti = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("T-" + ti);
                    try {
                        semaphore.acquire();  //获取许可，计数减1，获取不到的话阻塞
                        System.out.println(Thread.currentThread().getName() + "前:" + cnt);
                        cnt++;
                        System.out.println(Thread.currentThread().getName() + "后:" + cnt);
                        semaphore.release();  //还回许可，计数加1
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("================");
        exec.shutdown();

    }
}
