import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

/**
 * Created by migle on 2016/6/13.
 */
public class HDFSMetadata {
    static{
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            System.setProperty("hadoop.home.dir","E:\\spark\\hadoop-2.6.4\\hadoop-2.6");
            System.out.println("Win操作系统");
        }
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        Path f1 = new Path("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/data1.dat");
        FileSystem fs = f1.getFileSystem(conf);
        for (BlockLocation bl : fs.getFileBlockLocations(f1, 0, 10)) {
            for(String host : bl.getHosts()){
                System.out.println(host);
            }
            for(String name: bl.getTopologyPaths()){
                System.out.println(name);
            }
        }
        ;
    }
}
