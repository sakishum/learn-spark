

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
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "vm-centos-00:9092");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");


        String[] topics=new String[]{"sdi_scdt_3","sdi_scdt_4","sdi_scdt_5"};
        String topic ="";
        Producer<String, String> producer = new KafkaProducer<>(props);
        //producer.send(new ProducerRecord<String, String>("kafkatest", "hello kafka "));
        for(int i = 0; i < 100; i++){
            String msg = String.format("%s|%s|%s|%s",getPhoneNo(),new Random().nextInt(200),"m"+new Random().nextInt(1000), "20160810");
            topic = topics[new Random().nextInt(1)];
            System.out.println(topic + "  " + msg);
             Thread.sleep(1000);
            producer.send(new ProducerRecord<String, String>(topic, msg));
        }

        //producer.send(new ProducerRecord<String, String>("kafkatest", Integer.toString(i), Integer.toString(i)));
        // producer.close();
    }

    public static String  getPhoneNo()  {
        String[] pre = new String[]{"135586286","187973844"};

        return pre[new Random().nextInt(2)] + new Random().nextInt(10) + new Random().nextInt(10);
    }
}
