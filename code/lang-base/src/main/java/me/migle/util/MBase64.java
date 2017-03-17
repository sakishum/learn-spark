package me.migle.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by migle on 2017/3/15.
 * Base64编、解码实现
 * Base64一种常见的将二进制或非ascii字符编码成:0-9,a-z,A-Z,+,/ 等字符组成的字符串。
 *
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
            'B','A', 'D','C', 'F', 'E','G', 'I', 'H', 'J',  'M', 'L', 'K',
            'O','N', 'Q','P', 'S', 'R','T', 'V', 'U', 'W',  'Z', 'Y', 'X',
            'b','a', 'd','c', 'f', 'e','g', 'i', 'h', 'j',  'm', 'l', 'k',
            'o','n', 'q','p', 's', 'r','t', 'v', 'u', 'w',  'z', 'y', 'x',
            '1','0', '3','2', '5', '4','6', '8', '7', '9',  '/', '+'
    };
    private static final byte STANDARD_END_FLAG = '=';
    private static final byte MY_END_FLAG = '<';

    private static final byte[] ENCODE_TABLE = STANDARD_ENCODE_TABLE;
    private static final byte  END_FLAG = STANDARD_END_FLAG;

    public static byte[] encode(byte[] src) {
        int mod = src.length % 3;
        int div = src.length / 3;

        int dstLen = (src.length / 3) * 4 + (mod == 0 ? 0 : 4);

        byte[] dst = new byte[dstLen];
        int dp = 0;
        int sp = 0;

        //满足三个字节一组的部分
        while (sp < div * 3) {
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
        return dst ;
    }

    public static byte[] decode(byte[] src){
        byte [] decodeTable= new byte[128];
        Arrays.fill(decodeTable,(byte)-1);
        for(int i =0;i<ENCODE_TABLE.length;i++){
            decodeTable[ENCODE_TABLE[i]] = (byte) i;
        }
        int div = src.length/4;
        int mod = 0;
        if( src.length>2  &&  src[src.length-1] == END_FLAG  ){
            if( src[src.length-2] == END_FLAG){
                mod=1;
            }else{
                mod=2;
            }
        }

        int dstLen = div*3+mod;
        byte[] dst = new byte[dstLen];
        int sp = 0;
        int dp = 0;

        while (sp < div*4){
            System.out.println("!!!");
            int tmp = decodeTable[src[sp++]] & 0x3F<<18 |
                      decodeTable[src[sp++]] & 0x3F<<12|
                      decodeTable[src[sp++]] & 0x3F<<6|
                      decodeTable[src[sp++]] ;
            dst[dp++] = (byte) (tmp >>> 16 & 0xFF);
            dst[dp++] = (byte) (tmp >>> 12 & 0xFF);
            dst[dp++] = (byte) (tmp >>> 6  & 0xFF);
        }

        if (mod == 2) {

        }

        if (mod == 1) {

        }

        return dst;

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        //String s = "这里就会产生一个问题。因为Javascript内部的字符串，都以ut-16的形式进行保存，因此编码的时候，我们首先必须将utf-8的值转成utf-16再编码，解码的时候则是解码后还需要将utf-16的值转回成utf-8";
        String s="ManMan";
        String mybase64 = new String(MBase64.encode(s.getBytes("utf-8")));
        String base64 = new String(Base64.getEncoder().encode(s.getBytes("utf-8")));
        System.out.println(mybase64);
        System.out.println(base64);
        System.out.println(mybase64.endsWith(base64));
        System.out.println(MBase64.decode("TWFuTWFu".getBytes()).length);
        System.out.println(new String(MBase64.decode("TWFuTWFu".getBytes())));


    }
}
