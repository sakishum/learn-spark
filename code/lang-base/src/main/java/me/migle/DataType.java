package me.migle;

/**
 * Created by migle on 2016/7/22.
 */
public class DataType {

    public static void main(String[] args) {

        System.out.println(Integer.MAX_VALUE);
        //Integer相等的问题
        // 自动装箱机制，Integer.valueOf()  有缓存!
        Integer ia1 = 100;
        Integer ib1 = 100;
        System.out.println(ia1 == ib1); //true

        Integer ia2 = new Integer(100);
        Integer ib2 = new Integer(100);
        System.out.println(ia2 == ib2);  //false

        Integer ic = 200;
        Integer id = 200;
        System.out.println(ic == id);  //false


        int ix1 = new Integer(200);
        int ix2 = new Integer(200);
        System.out.println(ix1 == ix2); //true

    }
}
