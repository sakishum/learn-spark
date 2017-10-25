package demo;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author migle on 2017/10/19.
 */
public class RabbitProducerDemo {
    //4种类型的exchange
     enum ExchangeType {
        DIRECT,FANOUT ,TOPIC, HEADERS
    }

    static ExchangeType sendType = ExchangeType.HEADERS;
    //static String exchangeName="rabbit_demo";
    //static String exchangeName="rabbit_demo_direct";
    //static String exchangeName="rabbit_demo_topic";
    static String exchangeName="rabbit_demo_headers";

    public static void main(String[] args)
        throws IOException, TimeoutException, InterruptedException {

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
        String routingKey = queueName;
        BasicProperties props =  MessageProperties.PERSISTENT_TEXT_PLAIN;

        switch(sendType){
            case DIRECT:
                //默认exchange就是direct不需要特殊处理,(Empty string) and amq.direct:根据Binding指定的Routing Key，将符合Key的消息发送到Binding的Queue
                //direct类型的转发器背后的路由转发算法很简单：消息会被推送至绑定键（binding key）和消息发布附带的选择键（routing key）完全匹配的队列
                //如果有多个consumer监听了相同的routing key  则他们都会受到消息
                //最简单的发送方式,routingkey为队列名
                //任何发往这个exchange的消息都会被路由到routing key的名字对应的队列上，如果没有对应的队列，则消息会被丢弃
                /////channel.basicPublish("", queueName, null, message.getBytes());
                //以下代码测试发送至多个queue，发送时会发送到所有绑定的队列中,
                channel.exchangeDeclare(exchangeName, "direct");
                routingKey = queueName;
                //也可以在消费者处绑定
                channel.queueBind(queueName,exchangeName,routingKey);
                //再绑定一个,绑定之后才开始发送
                //channel.queueDeclare(queueName+"-001",false,false,false,null);
                //channel.queueBind(queueName+"-001",exchangeName,routingKey);
                //可以绑定到不同queue到routingkey，根据routingkey发送到不同的队列中。
                break;
            case FANOUT:
                //NOTE:
                //1. 广播给所有队列接收方也必须通过fanout交换机获取消息,所有连接到该交换机的consumer均可获取消息
                //2. 如果producer在发布消息时没有consumer在监听，消息将被丢弃
                //3. 给所有与此exchange绑定的queue发送消息，如果没有绑定则不发送

                channel.exchangeDeclare(exchangeName, "fanout");
                //channel.queueBind("xxxxx",exchangeName,"");
                routingKey="";
                break;
            case TOPIC:
                routingKey="rabbit.demo.topic";
                channel.exchangeDeclare(exchangeName,"topic");
                break;
            case HEADERS:
                //不是通过routing key来路由消息
                channel.exchangeDeclare(exchangeName,"headers");
                Map<String, Object> headers = new HashMap<String, Object>();
                headers.put("name", "my"); //定义headers
                headers.put("no", "14341");
                AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder().headers(headers);

                props = builder.build();
                break;
            default:
                System.out.println("错误的枚举类型");
                System.exit(-1);


        }
        System.out.println("routing key:"+routingKey);
        for (int i = 0; i < 100; i++) {
            //exchange:若为空则使用默认的exchange,default exchange，它用一个空字符串表示，它是direct exchange类型

            channel.basicPublish(exchangeName,routingKey, props,("java-msg-test-"+i).getBytes("utf-8"));
            //channel.basicPublish(exchangeName,"rabbit.demo2.topic", MessageProperties.PERSISTENT_TEXT_PLAIN,("java-msg-test-"+i).getBytes("utf-8"));

            System.out.println("java-msg-test-"+i);
            TimeUnit.MILLISECONDS.sleep(500);
        }
        channel.close();
        connection.close();
        factory.clone();
        System.out.println("over!");
    }


}
