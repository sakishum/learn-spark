package com.asiainfo.hive.udf;

/**
 * Created by migle on 2017/3/9.
 */
public class PhoneNoUtil {

    public static String encode(String phoneNo) {
        if (phoneNo.length() < 4) return phoneNo;
        StringBuffer r = new StringBuffer(phoneNo.substring(0,phoneNo.length()-4));
        r.append(Character.valueOf((char) (Integer.valueOf(getchar(phoneNo, -4).toString()) + 65)));
        r.append(Character.valueOf((char) (Integer.valueOf(getchar(phoneNo, -3).toString()) + 69)));
        r.append(Character.valueOf((char) (Integer.valueOf(getchar(phoneNo, -2).toString()) + 72)));
        r.append(Character.valueOf((char) (Integer.valueOf(getchar(phoneNo, -1).toString()) + 81)));
        return r.toString().toLowerCase();
    }

    public static String decode(String phoneNo) {
        if (phoneNo.length() < 4) return phoneNo;
        StringBuffer r = new StringBuffer(phoneNo.substring(0,phoneNo.length()-4));
        String tmp = phoneNo.toUpperCase();
        r.append(getchar(tmp, -4) - 65);
        r.append(getchar(tmp, -3) - 69);
        r.append(getchar(tmp, -2) - 72);
        r.append(getchar(tmp, -1) - 81);
        return r.toString().toLowerCase();
    }

    private static Character getchar(String str, int index) {
        if (index >= 0 && str.length() > index)
            return str.charAt(index);
        else
            return str.charAt(str.length() - Math.abs(index));
    }

    public static void main(String[] args) {
//        String str =  "13558628685";
//        String str2 = "18728608117";
//        String str3 = "13558807388";
//        System.out.println(encode(str3));
//        System.out.println(decode("1872860ifix"));
//
//        System.out.println(str.equals( decode(encode(str))));
//        System.out.println(str2.equals( decode(encode(str2))));
//        System.out.println(getchar(str,10));;
//        System.out.println(getchar(str,-1));;
//        System.out.println(getchar(str,-2));;
//        System.out.println(getchar(str,-3));;
//        System.out.println(getchar(str,-4));;
//        System.out.println(getchar(str,-11));;
//        System.out.println(getchar(str,0));;
    }
}
