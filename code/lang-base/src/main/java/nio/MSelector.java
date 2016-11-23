package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。
 * 这样，一个单独的线程可以管理多个channel，从而管理多个网络连接
 */
public class MSelector {
    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        //ServerSocket ss = new ServerSocket(9090);

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(9999));
        //ServerSocketChannel ssc = ss.getChannel();

        //与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，
        // 因为FileChannel不能切换到非阻塞模式。而套接字通道都可以
        ssc.configureBlocking(false);
        //注意register()方法的第二个参数。这是一个“interest集合”
        //意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型的事件：
        //Connect\Accept\Read \Write
        SelectionKey skey = ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int sint = selector.select();
            if (sint == 0) continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();

            System.out.println("------------------------"+sint);
            while (iterator.hasNext()) {
                System.out.println("*******************");
                SelectionKey k = iterator.next();
                if (k.isAcceptable()) {
                    System.out.println("===========accept");
                    ServerSocketChannel server = (ServerSocketChannel) k.channel();
                    SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    client.write(ByteBuffer.wrap(("hello" + client.getRemoteAddress()).getBytes()));
                    client.register(selector, SelectionKey.OP_READ);
                    //System.out.println("send message");

                } else if (k.isReadable()) {
                    System.out.println("========read");
                    SocketChannel client = (SocketChannel) k.channel();
                    ByteBuffer b = ByteBuffer.allocate(100);
                    try {
                        int read = client.read(b);
                        System.out.println("received:" + new String(b.array()));
                        client.write(ByteBuffer.wrap(("hello" + client.getRemoteAddress()).getBytes()));
                    } catch (IOException ex) {
                        //如果客户端已关闭，需要把对应的SocketChannel也关闭，否则会死循环
                        // 但怎么判断客户端是不是真的已关闭，设个retry times ?
                            client.close();
                    }
                }
                System.out.println("remove!");
                iterator.remove();
            }

        }
    }
}
