import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author migle on 2017/10/19.
 */
public class RabbitProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String queueName="hello";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.131");
        factory.setPort(5672);
        factory.setUsername("cqcrm");
        factory.setPassword("cqcrm");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDelete(queueName);
        //durable:持久保存此queue，服务重启后仍然存在(注意是queue不是queue中的消息)
        //exclusiv:
        //autoDelete:当连接断开时是否自动删除队列
        channel.queueDeclare(queueName,false,false,false,null);

        for (int i = 0; i < 100; i++) {
            channel.basicPublish("",queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,("java-msg-"+i).getBytes("utf-8"));
            System.out.println("java-msg-"+i);
        }
        channel.close();
        connection.close();
        factory.clone();
        System.out.println("over!");
    }
}
