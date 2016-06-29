import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by migle on 2016/6/29.
 */
public class MProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "vm-centos-00:9092,vm-centos-01:9092");
        //props.put("metadata.broker.list", "vm-centos-00:9092,vm-centos-01:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 335544);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<>(props);
        //producer.send(new ProducerRecord<String, String>("kafkatest", "hello kafka "));
        for(int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("kafkatest", Integer.toString(i), Integer.toString(i)));

        producer.close();
    }
}
