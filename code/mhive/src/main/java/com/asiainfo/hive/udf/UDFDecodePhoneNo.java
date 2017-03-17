package com.asiainfo.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Created by migle on 2017/3/9.
 *  hive中自定义函数添加
 *  CREATE FUNCTION decode_phone_no AS 'com.asiainfo.hive.udf.UDFDecodePhoneNo'  USING JAR 'hdfs:///udf/mhive-1.0-SNAPSHOT.jar';
 *
 */
public class UDFDecodePhoneNo extends UDF {
    @Description(name = "decode_phone_no",
            value = "_FUNC_(val) - 解码手机号码")
    public String evaluate(String val){
        return PhoneNoUtil.decode(val);
    }
}
