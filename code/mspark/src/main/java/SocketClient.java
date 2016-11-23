import java.io.*;
import java.net.Socket;

/**
 * Created by migle on 2016/7/1.
 */
public class SocketClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket client = new Socket("localhost",9999);
        InputStream ins = client.getInputStream();
        OutputStream ops = client.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
        byte []buf = new byte[1024];
        int i = 0;
        while(true){
            System.out.println("=======");
            int len = ins.read(buf);
            System.out.println(new String(buf,0,len));
            bw.write("hello server + " +i);
            bw.flush();
            ops.flush();
            i++;
            Thread.sleep(1000);
        }
        

    }
}
