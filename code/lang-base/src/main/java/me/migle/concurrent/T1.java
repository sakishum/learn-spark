package me.migle.concurrent;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author migle on 2017/8/16.
 */
public class T1 {

    public static void main(String[] args) {
        ExecutorService ex = Executors.newCachedThreadPool();
        TH1 th1 = new TH1();
        for (int i = 0; i < 100; i++) {
            ex.execute(new TT1(th1));
            //ex.execute(new TT1(new TH1()));
        }


    }
}

class TT1 extends Thread {

    private TH1 th1;

    public TT1(TH1 t) {
        this.th1 = t;
    }

    @Override
    public void run() {
        try {
            th1.handle(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class TH1 {
    static ReentrantLock lock = new ReentrantLock();
    ThreadLocal<Integer> jj = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return new Integer(10);
        }
    };
    Integer ll = 10;
 // j and jj is Thread-safe,synchronized for ll;
  //  public synchronized void handle(int i) throws InterruptedException {
    public  void handle(int i) throws InterruptedException {
        int j = 10;
        //lock.tryLock();
        //j and jj is Thread-safe,lock for ll;
        lock.lock();
        try{
            j = j + i;
            jj.set(jj.get() + i);
            ll += i;
            //TimeUnit.SECONDS.sleep(1);
            j -= i;
            jj.set(jj.get() - i);
            ll -= i;
            System.out.println(Thread.currentThread().getName() + "  j:" + j + " jj:" + jj.get() + " ll:" + ll);
        }finally {
            lock.unlock();
        }

    }

}
