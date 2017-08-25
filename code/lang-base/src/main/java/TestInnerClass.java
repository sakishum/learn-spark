import com.asisinfo.ds.ClassDemo;
import com.asisinfo.ds.ClassDemo.InnerClass1;
import java.util.concurrent.TimeUnit;

/**
 * @author migle on 2017/8/24.
 */
public class TestInnerClass {

    public static void main(String[] args) {
        ClassDemo cd = new ClassDemo();
        ClassDemo.InnerClass1 ci2 =  cd.new InnerClass1();
        ci2.setIt1("inner class ");

        InnerClass1 getit = cd.getit();
        System.out.println(getit.getIt1());

    }
}
