import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;


/**
 * Created by migle on 2016/6/24.
 */
//import static MJUtil.println;

public class HBaseAPIDemo {

    public static void main(String[] args) throws IOException {
        new HBaseAPI().createTable("mtest","cf1");

    }

}
//Creating table. Exception in thread "main" org.apache.hadoop.hbase.DoNotRetryIOException: org.apache.hadoop.hbase.DoNotRetryIOException: java.lang.RuntimeException: native snappy library not available: this version of libhadoop was built without snappy support. Set hbase.table.sanity.checks to false at conf or table descriptor if you want to bypass sanity checks

class HBaseAPI {
    private Configuration conf = HBaseConfiguration.create();

    public HBaseAPI() {
        createConf();
    }

    private void createConf() {
        //Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.rootdir", "hdfs://vm-centos-01:9999/hbase");
        conf.set("hbase.master.port", "16000");
        conf.set("hbase.zookeeper.quorum", "vm-centos-01");
    }

    public void createTable(String tablename, String... cfs) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(conf);
             Admin admin = connection.getAdmin()) {

            HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tablename));
            for (String cf : cfs) {
                table.addFamily(new HColumnDescriptor(cf).setCompressionType(Compression.Algorithm.SNAPPY));
            }
            System.out.print("Creating table. ");
            if (admin.tableExists(table.getTableName())) {
                admin.disableTable(table.getTableName());
                admin.deleteTable(table.getTableName());
            }
            admin.createTable(table);
            System.out.println(" Done.");
        }
    }

}
