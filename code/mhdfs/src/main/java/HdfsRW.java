import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * Created by migle on 2016/6/8.
 */
public class HdfsRW {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS","hdfs://vm-centos-01:9999");
        //FileSystem.get(conf);

        Path p1 = new Path("hdfs://vm-centos-01:9999/user/migle/test/t1");
        FileSystem fs = p1.getFileSystem(conf);
        if (fs.exists(p1)) {
            fs.delete(p1, true);
            System.out.println("存在则删除");
        } else {
            fs.mkdirs(p1);
            System.out.println("为存在则创建");
        }

        fs.close();
    }
}

