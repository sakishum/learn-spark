package me.migle.util;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by migle on 2017/3/15.
 * Base64编、解码实现
 * Base64一种常见的将二进制或包含非ascii字符的字符串编码成:0-9,a-z,A-Z,+,/ 等字符组成的字符串。
 * <p>
 * 规则见：http://www.ruanyifeng.com/blog/2008/06/base64.html
 */
public class MBase64 {
    private static final byte[] STANDARD_ENCODE_TABLE = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };


    private static final byte[] MY_ENCODE_TABLE = {
            'B', 'A', 'D', 'C', 'F', 'E', 'G', 'I', 'H', 'J', 'M', 'L', 'K',
            'O', 'N', 'Q', 'P', 'S', 'R', 'T', 'V', 'U', 'W', 'Z', 'Y', 'X',
            'b', 'a', 'd', 'c', 'f', 'e', 'g', 'i', 'h', 'j', 'm', 'l', 'k',
            'o', 'n', 'q', 'p', 's', 'r', 't', 'v', 'u', 'w', 'z', 'y', 'x',
            '1', '0', '3', '2', '5', '4', '6', '8', '7', '9', '/', '+'
    };
    private static final byte STANDARD_END_FLAG = '=';
    private static final byte MY_END_FLAG = '<';

    private static final byte[] ENCODE_TABLE = STANDARD_ENCODE_TABLE;
    private static final byte END_FLAG = STANDARD_END_FLAG;

    public static byte[] encode(byte[] src) {
        int mod = src.length % 3;
        int div = src.length / 3;

        int dstLen = (src.length / 3) * 4 + (mod == 0 ? 0 : 4);

        byte[] dst = new byte[dstLen];
        int dp = 0;
        int sp = 0;

        //满足三个字节一组的部分
        while (sp < div * 3) {  //FIXME
            int tmp = (src[sp++] & 0XFF) << 16 |
                    (src[sp++] & 0XFF) << 8 |
                    (src[sp++] & 0XFF);
            dst[dp++] = ENCODE_TABLE[tmp >>> 18 & 0x3f];
            dst[dp++] = ENCODE_TABLE[tmp >>> 12 & 0x3f];
            dst[dp++] = ENCODE_TABLE[tmp >>> 6 & 0x3f];
            dst[dp++] = ENCODE_TABLE[tmp & 0x3f];
        }
        //剩两个字节的情况,2byte=16bit,从左侧开始取每6位前补两位0，剩余4位前后各补两位0，最后加一个'='
        if (mod == 2) {
            int tmp = (src[sp++] & 0XFF) << 8 | src[sp++] & 0XFF;
            dst[dp++] = ENCODE_TABLE[tmp >>> 10 & 0x3F];
            dst[dp++] = ENCODE_TABLE[tmp >>> 4 & 0x3F];
            dst[dp++] = ENCODE_TABLE[tmp << 2 & 0x3C];
            dst[dp++] = END_FLAG;
        }
        //剩一个字节的情况,1byte=8bit,从左侧开始取每6位前补两位0，剩余2位前补两位0，后补4位0，最后加两个'='
        if (mod == 1) {
            int tmp = src[sp++] & 0XFF;
            dst[dp++] = ENCODE_TABLE[tmp >>> 2 & 0x3F];
            dst[dp++] = ENCODE_TABLE[tmp << 4 & 0x30];
            dst[dp++] = END_FLAG;
            dst[dp++] = END_FLAG;
        }
        return dst;
    }

    public static byte[] decode(byte[] src) {
        byte[] decodeTable = new byte[128];
        Arrays.fill(decodeTable, (byte) -1);
        for (int i = 0; i < ENCODE_TABLE.length; i++) {
            decodeTable[ENCODE_TABLE[i]] = (byte) i;
        }
        int div = src.length / 4;
        int mod = 0;
        if (src.length > 2 && src[src.length - 1] == END_FLAG) {
            if (src[src.length - 2] == END_FLAG) {
                mod = 1;
            } else {
                mod = 2;
            }
        }

        int dstLen = (div - 1) * 3 + mod;   //结果数据组长度
        byte[] dst = new byte[dstLen];

        int sp = 0;
        int dp = 0;
        int flen = (div - (mod > 0 ? 1 : 0)) * 4;
        while (sp < flen) {
            int tmp = (decodeTable[src[sp++]] & 0x3F) << 18 |
                    (decodeTable[src[sp++]] & 0x3F) << 12 |
                    (decodeTable[src[sp++]] & 0x3F) << 6 |
                    (decodeTable[src[sp++]] & 0x3F);
            dst[dp++] = (byte) (tmp >> 16);
            dst[dp++] = (byte) (tmp >> 8);
            dst[dp++] = (byte) tmp;
        }

        if (mod == 2) {
            int tmp = ((decodeTable[src[sp++]] & 0x3F) << 10 |
                    (decodeTable[src[sp++]] & 0x3F) << 4 |
                    (decodeTable[src[sp++]] & 0x3F) >> 2) & 0XFFFF;

            dst[dp++] = (byte) (tmp >> 8);
            dst[dp++] = (byte) tmp;

        }

        if (mod == 1) {
            dst[dp++] = (byte) ((decodeTable[src[sp++]] & 0x3F) << 2 |
                    (decodeTable[src[sp++]] & 0x3F) >>> 2);

        }

        return dst;

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "这里就会产生一个问题。因为Javascript内部的字符串，都以ut-16的形式进行保存，因此编码的时候们首先必须将utf-8的值转成utf-16再编码，解码的时候则是解码后还需要将utf-16的值转回成utf-8";
        //String s = "ManManm";

        String mybase64 = new String(MBase64.encode(s.getBytes("utf-8")));
        String base64 = new String(Base64.getEncoder().encode(s.getBytes("utf-8")));

        System.out.println("encode:" + mybase64);
        System.out.println("encode is correct:" + mybase64.equals(base64));
        System.out.println("decode is correct:" + s.equals(new String(MBase64.decode(mybase64.getBytes()))));


//        try {
//            //图片编码测试
//            File f = new File("/Users/migle/Downloads/test.png");
//            FileImageInputStream image = new FileImageInputStream(f);
//            byte[] data = new byte[(int) f.length()];
//            image.read(data, 0, data.length);
//            image.close();
//            //把图片编码成字符串
//            String mimg64 = new String(MBase64.encode(data));
//            String img64 = new String(Base64.getEncoder().encode(data));
//            System.out.println(mimg64);
//            System.out.println(img64);
//            System.out.println("encode img is correct:" + mimg64.equals(img64));
//
//            //解码后写回文件
//            FileOutputStream fout = new FileOutputStream(new File("/Users/migle/Downloads/test-2.png"));
//            byte[] decode = MBase64.decode(mimg64.getBytes());
//            fout.write(decode, 0, decode.length);
//            fout.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
