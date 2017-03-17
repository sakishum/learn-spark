package com.asiainfo.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Created by migle on 2017/3/9.
 * hive中自定义函数添加
 * CREATE FUNCTION encode_phone_no AS 'com.asiainfo.hive.udf.UDFEncodePhoneNo'  USING JAR 'hdfs:///udf/mhive-1.0-SNAPSHOT.jar';
 */
public class UDFEncodePhoneNo extends UDF {
    @Description(name = "encode_phone_no",
            value = "_FUNC_(val) - 编码手机号码")
    public String evaluate(String val){
        return PhoneNoUtil.encode(val);
    }
}
