import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by migle on 2016/6/27.
 */
public class AboutSemaphore {
    private  static int cnt = 0;

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(1);

        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int ti = i;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("T-" + ti);
                    try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "前:" + cnt);
                    cnt++;
                    System.out.println(Thread.currentThread().getName() + "后:" + cnt);
                    semaphore.release();
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
