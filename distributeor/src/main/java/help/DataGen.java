package help;
import org.apache.kafka.clients.producer.KafkaProducer;
import  org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Random;


/**
 * 生成测试数据
 */
public class DataGen {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        //props.put("bootstrap.servers", "vm-centos-00:9092,vm-centos-01:9092");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "vm-centos-00:9092");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");


        String[] topics=new String[]{"topic-1","topic-2"};
        String topic ="";
        Producer<String, String> producer = new KafkaProducer<>(props);
        //producer.send(new ProducerRecord<String, String>("kafkatest", "hello kafka "));
        for(int i = 0; i < 10; i++){
            String msg = String.format("%s|%s|%s|%s",getPhoneNo(),new Random().nextInt(200),"m"+new Random().nextInt(1000), "20160810");
            //topic = topics[new Random().nextInt(2)];
            System.out.println(msg);
            //Thread.sleep(2000);

            producer.send(new ProducerRecord<String, String>("test-1","K:"+msg, msg));
        }

        //producer.send(new ProducerRecord<String, String>("kafkatest", Integer.toString(i), Integer.toString(i)));
        producer.close();
    }

    public static String  getPhoneNo()  {
        String[] pre = new String[]{"135586286","187973844"};

        return pre[new Random().nextInt(2)] + new Random().nextInt(10) + new Random().nextInt(10);
    }
}
