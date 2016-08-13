import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;

/**
 * Created by migle on 16/8/13.
 */
public class Output implements Serializable {

    //输出至kafak
    private String msg;
    private String topic;

    public Output(String topic, String msg){
        this.topic = topic;
        this.msg = msg;
    }
    public void output(KafkaProducer<String,String> producer){
        System.out.println("send:" + this.msg);
        //ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topic, null, this.msg);
        producer.send(new ProducerRecord<String, String>(this.topic, null, this.msg));
    }

    @Override
    public String toString() {
        return "Output{" +
                "msg='" + msg + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
