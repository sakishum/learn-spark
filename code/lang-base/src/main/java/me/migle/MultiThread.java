package me.migle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by migle on 2016/7/25.
 */
public class MultiThread {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        Lock lock = new ReentrantLock();
        ExecutorService  executor = Executors.newFixedThreadPool(10);
        for(int i = 0;i<1000;i++){
            executor.execute(new MPrinter(lock));
        }
        executor.shutdown();
    }
}

class MPrinter implements  Runnable{
    static int i = 0;
//    Semaphore semaphore;
//   public MPrinter( Semaphore semaphore ){
//        this.semaphore = semaphore;
//    }

      Lock lock ;
       public MPrinter( Lock lock  ){
            this.lock =lock;
    }
    @Override
    public void run() {
            //semaphore.acquire();
        try {
            if(lock.tryLock(1000, TimeUnit.SECONDS)){
                System.out.println(i++);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        }finally{
            lock.unlock();
        }

        //semaphore.release();
    }
}