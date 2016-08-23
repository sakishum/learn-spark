package com.asiainfo.common;

import com.asiainfo.Conf;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by migle on 2016/8/22.
 */
public class KafkaProducerPool{
   private final static Map<String,Object> props = new HashMap<String, Object>();
    static{

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Conf.kafka);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        //props.put(ProducerConfig.BATCH_SIZE_CONFIG,10);
    }
    public static KafkaProducer producer(){
        return ProducerHolder.producer;
    }

    private static class ProducerHolder{
     final static KafkaProducer<String,Object> producer = new KafkaProducer<String,Object>(props);
        static {
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    if(producer != null){
                        System.out.println("close producer");
                        producer.close();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            KafkaProducerPool.producer().send(new ProducerRecord("qcd_ruselt_netpay_1", null,"hello"));
            System.out.println("================");
            Thread.sleep(1000);

        }

    }
}
