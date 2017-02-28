package org.apache.spark.mhadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Created by migle on 2016/11/12.
 */
public class RegPhoneno extends UDF {
    @Description(name="reg_phone",value="replace 86 or +86 in start")
    public String evaluate(String val) {
        return val.replaceAll("^86|\\+86", "");
    }
}
