package demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author migle on 2017/10/19.
 */
public class RabbitProducerDemo {
    //4种类型的exchange
     enum ExchangeType {
        DIRECT,FANOUT ,TOPIC, HEADERS
    }

    static ExchangeType sendType = ExchangeType.FANOUT;
    static String exchangeName="rabbit_demo";

    public static void main(String[] args) throws IOException, TimeoutException {

        String queueName="hello";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.131");
        factory.setPort(5672);
        factory.setUsername("cqcrm");
        factory.setPassword("cqcrm");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //channel.queueDelete(queueName);

        //durable:持久保存此queue，服务重启后仍然存在(注意是queue不是queue中的消息)
        //exclusiv:队列是否是独占的，如果为true只能被一个connection使用，其他连接建立时会抛出异常
        //autoDelete:当连接断开时是否自动删除队列没有连接且没有未处理的消息
        //arguments：建立队列时其他参数
        channel.queueDeclare(queueName,false,false,false,null);

        switch(sendType){
            case DIRECT:
                //默认exchange就是direct不需要特殊处理,(Empty string) and amq.direct:根据Binding指定的Routing Key，将符合Key的消息发送到Binding的Queue
                //向指定的队列发送消息，消息只会被一个consumer处理,多个消费者消息会轮训处理,消息发送时如果没有consumer，消息不会丢失
                break;
            case FANOUT:
                //广播给所有队列  接收方也必须通过fanout交换机获取消息,所有连接到该交换机的consumer均可获取消息
                //如果producer在发布消息时没有consumer在监听，消息将被丢弃
                channel.exchangeDeclare(exchangeName, "fanout");
                queueName="";

            case TOPIC:

        }
        for (int i = 0; i < 100; i++) {
            //exchange:若为空则使用默认的exchange,default exchange，它用一个空字符串表示，它是direct exchange类型

            channel.basicPublish(exchangeName,queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,("java-msg-"+i).getBytes("utf-8"));

            System.out.println("java-msg-"+i);
        }
        channel.close();
        connection.close();
        factory.clone();
        System.out.println("over!");
    }


}
