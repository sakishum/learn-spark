import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by migle on 2016/7/1.
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost",9999);
        InputStream ins = client.getInputStream();
        byte []buf = new byte[1024];
        while(true){
            int len = ins.read(buf);
            System.out.println(new String(buf,0,len));
        }

    }
}
