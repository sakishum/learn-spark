package me.migle.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by migle on 2017/3/15.
 */
public class MBase64 {
    private static final byte[] STANDARD_ENCODE_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static final byte END_FLAG='=';

    public static String encode(byte[] src) {
//        StringBuffer s = new StringBuffer();
        System.out.println(src.length);
        int mod = src.length % 3;
        int div = src.length / 3;
        //int len = (src.length/3)*4 + (mod == 0 ? 0 : mod + 1) ;
        int len = (src.length / 3) * 4 + (mod == 0 ? 0 : 4);
        byte[] dst = new byte[len];
        int dp = 0;
        int sp = 0;
        while ( sp < div*3) {
            int tmp = (src[sp++] & 0XFF) << 16 |
                    (src[sp++] & 0XFF) << 8 |
                    (src[sp++] & 0XFF);
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 18 & 0x3f];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 12 & 0x3f];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 6 & 0x3f];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp & 0x3f];
//            s.append((char)STANDARD_ENCODE_TABLE[tmp>>>18 & 0x3f]);
//            s.append((char)STANDARD_ENCODE_TABLE[tmp>>>12 & 0x3f]);
//            s.append((char)STANDARD_ENCODE_TABLE[tmp>>>6 & 0x3f]);
//            s.append((char)STANDARD_ENCODE_TABLE[tmp & 0x3f]);
        }

        if (mod == 2) {
            int tmp = (src[sp++] & 0XFF) << 8 | src[sp++] & 0XFF ;
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 10 & 0x3F];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 4 & 0x3F];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp << 2 & 0x3C];
            dst[dp++] = END_FLAG;
        }
        if (mod == 1) {
            int tmp = src[sp++] & 0XFF ;
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp >>> 2 & 0x3F];
            dst[dp++] = (byte) STANDARD_ENCODE_TABLE[tmp << 4 & 0x30];
            dst[dp++] = END_FLAG;
            dst[dp++] = END_FLAG;
        }

        for (byte b : dst) {
            System.out.println(b);
        }
        return new String(dst);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "这里就会产生一个问题。因为Javascript内部的字符串，都以utf-16的形式进行保存，因此编码的时候，我们首先必须将utf-8的值转成utf-16再编码，解码的时候则是解码后还需要将utf-16的值转回成utf-8";
        String mybase64 = MBase64.encode(s.getBytes("utf-8"));
        String base64 = new String(Base64.getEncoder().encode(s.getBytes("utf-8")));
        System.out.println(mybase64);
        System.out.println(base64);
        System.out.println(mybase64.endsWith(base64));

    }
}
