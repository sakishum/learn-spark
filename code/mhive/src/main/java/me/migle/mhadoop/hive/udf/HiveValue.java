/**
 * Created by migle on 2016/6/3.
 */

package me.migle.mhadoop.hive.udf;


import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;


public class HiveValue extends UDF{
    @Description(name="vlaue",value="like value() in DB2")
    public String evaluate(String val, String defValue) {
        return val == null ? defValue : val;
    }
}
