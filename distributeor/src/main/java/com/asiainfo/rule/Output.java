package com.asiainfo.rule;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by migle on 16/8/13.
 */
public class Output implements Serializable {

    private Map<String, String> data;
    private String topic;
    private boolean hasData =false;

    public Output(String topic, Map<String, String> data){
        this.topic = topic;
        this.data = data;
        this.hasData = true;
    }

    public Output(){
        this.hasData = false;
    }

    public boolean hasData() {
        return hasData;
    }

    //todo:由传入的函数处理???
    //输出至kafak
    public void output(KafkaProducer<String,String> producer){
        //TODO 判断事件类型，输出成指定格式
        String msg = "{ruldid:" + data.get("ruleid")+ " eventid:" + data.get("eventid") + " phone_no:" + data.get("phone_no") + "payment_fee:" + data.get("payment_fee") + "}";
        System.out.println("send:" + msg);
        //ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topic, null, this.msg);
        producer.send(new ProducerRecord<String, String>(this.topic, null, msg));
    }

    @Override
    public String toString() {
        return "com.asiainfo.rule.Output{" +
                "data='" + data + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
