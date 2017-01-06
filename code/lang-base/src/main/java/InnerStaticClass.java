/**
 * Created by migle on 2017/1/6.
 */
public class InnerStaticClass {
    public static class ISC{

    }

    public static void main(String[] args) {
        ISC i1 = new ISC();
        ISC i2 = new ISC();
        System.out.println(i1);
        System.out.println(i2);
    }
}
