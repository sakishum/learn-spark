package demo;

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
        channel.basicQos(1); //server push消息时的队列长度
       switch (RabbitProducerDemo.sendType){
           case DIRECT:

               break;
           case FANOUT:
               channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
               String queueName = channel.queueDeclare().getQueue();
               channel.queueBind(queueName, EXCHANGE_NAME, "");
               break;

       }

        boolean autoack = false;
        channel.basicConsume(queueName, autoack, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                BasicProperties properties,
                byte[] body) throws IOException {
                System.out.println("rcv" + new String(body));
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    channel.abort();
                }finally {
                    //如果autoack=false,而没有这一句的话服务器不知道此消息已被接收
                    channel.basicAck(envelope.getDeliveryTag(),false);
                    //basicQos(1);保证一次只分发一个 。autoAck是否自动回复，如果为true的话，每次生产者只要发送信息就会从内存中删除，那么如果消费者程序异常退出，那么就无法获取数据，我们当然是不希望出现这样的情况，所以才去手动回复，每当消费者收到并处理信息然后在通知生成者。最后从队列中删除这条信息。如果消费者异常退出，如果还有其他消费者，那么就会把队列中的消息发送给其他消费者，如果没有，等消费者启动时候再次发送。
                }
            }
        });



    }
}
