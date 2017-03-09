package org.apache.spark.mhadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by migle on 2017/3/9.
 */
@Description(name = "PhoneEncrypt",
        value = "_FUNC_(str,pwd):解密手机号码",
        extended = "AES解密实现,为解密手机号码而添加，但其实可以通用")
public class UDFAESDecrypt {
    private KeyGenerator kgen;
    public UDFAESDecrypt() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public String evaluate(String str,String pwd) {

        try {
            kgen.init(128, new SecureRandom(pwd.getBytes("utf-8")));
            SecretKey secretKey = kgen.generateKey();// 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();// 返回基本编码格式的密钥
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化为解密模式的密码器
            byte[] result = cipher.doFinal(str.getBytes("utf-8"));
            return new String(result); // 明文
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return str;
    }
}
