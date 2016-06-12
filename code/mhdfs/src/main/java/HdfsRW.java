import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by migle on 2016/6/8.
 */
public class HdfsRW {
    static{
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            System.setProperty("hadoop.home.dir","E:\\spark\\hadoop-2.6.4\\hadoop-2.6");
            System.out.println("Win操作系统");
        }
    }
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        //conf.set("fs.defaultFS","hdfs://vm-centos-01:9999");
        //FileSystem.get(conf);

        Path p1 = new Path("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/");
        Path f1 = new Path("hdfs://vm-centos-01:9999/user/migle/HdfsRWTest/data1.dat");
        FileSystem fs = p1.getFileSystem(conf);
        System.out.println(fs.getClass().getCanonicalName());
        if (!fs.exists(p1)) {
            fs.mkdirs(p1);
            System.out.println("不存在则创建");
        }


        fs.deleteOnExit(f1);
        System.out.println("存在则先删除");
        FSDataOutputStream fsout =  fs.create(f1);

        FileInputStream fin = new FileInputStream("./mhdfs/src/main/resources/kv1.txt");

        //FIXME 为毛大小设为512时只能生成个空文件
        byte[] bs = new byte[4096];  //copyBytes(in, out, conf.getInt("io.file.buffer.size", 4096),  close);
        int n = 0;

        while( (n = fin.read(bs)) > 0){
            fsout.write(bs,0,n);
            System.out.println("写入字节："+n);
        }

//      fs.copyFromLocalFile(false,new Path("./mhdfs/src/main/resources/kv1.txt"),f1);

        fsout.close();
        fin.close();

        fs.close();
    }
}

