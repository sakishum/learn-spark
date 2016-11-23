import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;



/**
 * Created by migle on 2016/6/24.
 */
//import static MJUtil.println;

public class AboutFile {
    static void ff() throws MalformedURLException {
        System.out.println(new File(".").toURI().toURL());
    }
    public static void main(String[] args) throws MalformedURLException {
        //ff();
       System.out.println( InetAddress.getLoopbackAddress());
    }
}
