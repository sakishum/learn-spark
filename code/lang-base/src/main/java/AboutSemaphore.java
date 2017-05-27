import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by migle on 2016/6/27.
 */
public class AboutSemaphore {
    private  static int cnt = 0;

    public static void main(String[] args) {

        Semaphore semaphore = new Semaphore(1);  //只有一个许可
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
