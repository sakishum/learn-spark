package org.apache.spark.mhadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;

/**
 * Created by migle on 2016/11/12.
 */
public class RowKey {
    @Description(name="reg_phone",value="get rowkey for hbase")
    public String evaluate( String msisdn,  String time, String line) {
        String rowkey = new MD5RowKeyGenerator().generatePrefix(msisdn) + msisdn + time + new MD5RowKeyGenerator().generatePrefix(line);
        return rowkey;
    }
}
