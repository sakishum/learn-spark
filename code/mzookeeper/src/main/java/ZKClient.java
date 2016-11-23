import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * Created by migle on 16/9/7.
 */
public class ZKClient {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper("localhost:2181", 300000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("发生了!：" + event.getType() + "事件！");
            }
        });
        //zk.create("/test","hello".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        Stat s = new Stat();

        byte[] data = zk.getData("/zktest",false,s);
        System.out.println(new String(data));
        System.out.println(s.getAversion());
        System.out.println(s.getDataLength());
        System.out.println(s.toString());
        zk.close();

    }
}
