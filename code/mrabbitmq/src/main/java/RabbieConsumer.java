import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author migle on 2017/10/19.
 */
public class RabbieConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        String queueName = "hello";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.131");
        factory.setPort(5672);
        factory.setUsername("cqcrm");
        factory.setPassword("cqcrm");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //
        //channel.queueDeclare(queueName,true,false,false,null);
        boolean autoack = true;  //?一个消费者挂掉后另一个不会重新读？

        channel.basicConsume(queueName, autoack, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                BasicProperties properties,
                byte[] body) throws IOException {
                System.out.println("rcv" + new String(body));

                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    channel.abort();
                }finally {
                    //channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        });
    }
}
