import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by migle on 2016/6/30.
 */
public class SocketSender {
    public static void main(String[] args) throws IOException, InterruptedException {

        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();
        System.out.println("连接成功......");
        OutputStream os = socket.getOutputStream();

        Scanner scanner = new Scanner(System.in);;
        os.write("hello spark".getBytes());
        os.flush();

        while (scanner.hasNext()) {
            os.write(scanner.next().getBytes());
            os.flush();
        }
    }
}
