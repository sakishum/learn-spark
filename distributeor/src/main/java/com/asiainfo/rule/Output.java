package com.asiainfo.rule;

import com.asiainfo.Conf;
import com.asiainfo.common.KafkaProducerPool;
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
    private boolean hasData = false;

    public Output( String topic ,Map<String, String> data) {
        this.topic = topic;
        this.data = data;
        this.hasData = !data.isEmpty();
    }

    public Output() {
        this.hasData = false;
    }

    public boolean hasData() {
        return hasData;
    }

    //todo:由传入的函数处理???
    //输出至kafak
    public void output() {
        //TODO 判断事件类型，输出成指定格式
//        String msg = "{ruldid:" + data.get("ruleid")+ " eventid:" + data.get("eventid") + " phone_no:" + data.get("phone_no") + "payment_fee:" + data.get("payment_fee") + "}";
//        System.out.println("send:" + msg);
//        //ProducerRecord<String, String> record = new ProducerRecord<String, String>(this.topic, null, this.msg);
//        producer.send(new ProducerRecord<String, String>(this.topic, null, msg));

        String msg = "";
        //String topic = "";

        switch (data.get("eventid")) {
            case Conf.eventUSIMChange:
                msg = String.format("{\"ruleid\":\"%s\",\"eventid\":\"%s\",\"phone_no\":\"%s\",\"date\":\"%s\"}", data.get("ruleid"),data.get("eventid"),data.get("phone_no"),data.get("date"));
                KafkaProducerPool.producer().send(new ProducerRecord<String, String>(this.topic, null, msg));
                break;
            case Conf.eventNetpay:
                msg = String.format("{\"ruleid\":\"%s\",\"eventid\":\"%s\",\"phone_no\":\"%s\",\"payment_fee\":\"%s\",\"login_no\":\"%s\",\"date\":\"%s\"}",
                        data.get("ruleid"),data.get("eventid"),data.get("phone_no"),data.get("payment_fee"),data.get("login_no"),data.get("date"));
                System.out.println(msg);
                System.out.println("****************************");
                KafkaProducerPool.producer().send(new ProducerRecord<String, String>(this.topic, null, msg));
                break;
            case Conf.eventBusiOrder:
                msg = String.format("{\"ruleid\":\"%s\",\"eventid\":\"%s\",\"phone_no\":\"%s\",\"prod_prcid\":\"%s\",\"date\":\"%s\"}", data.get("ruleid"),data.get("eventid"),data.get("phone_no"),  data.get("prod_prcid"),data.get("date"));
                KafkaProducerPool.producer().send(new ProducerRecord<String, String>(this.topic, null, msg));
                break;
            default:
                System.out.println("error event id,do nothing!");
        }
    }

    @Override
    public String toString() {
        return "com.asiainfo.rule.Output{" +
                "data='" + data + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
