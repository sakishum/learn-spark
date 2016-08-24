package com.asiainfo.common;


import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;

import java.util.*;

/**
 * Created by migle on 2016/8/23.
 * 获取(topic,partition)的offset的工具类
 */
public class KafkaTopicOffsetTool {
    public static void main(String[] args) {

        Set<String> topics = new HashSet<>();
        topics.add("test-1");
        topics.add("test-2");
        topics.add("test3");

        Map<TopicAndPartition, Long> largstOffsets = getLargstOffsets(topics);

        for (Map.Entry<TopicAndPartition, Long> entry : largstOffsets.entrySet()) {
            System.out.println(entry.getKey().topic() + " " + entry.getKey().partition() + " " + entry.getValue());
        }


//        TopicMetadataRequest tmr = new TopicMetadataRequest(Arrays.asList(topics));
//        SimpleConsumer consumer = new SimpleConsumer("vm-centos-00", 9092, 100000, 64 * 1024, "leaderLookup");
//        TopicMetadataResponse msg = consumer.send(tmr);
//        Map<TopicAndPartition, PartitionOffsetRequestInfo> tp = new HashMap<>();
//
//        for (TopicMetadata tm : msg.topicsMetadata()) {
//            System.out.println(tm.topic());
//            for (PartitionMetadata pm : tm.partitionsMetadata()) {
//                System.out.println(pm.partitionId());
//                for (Broker broker : pm.replicas()) {
//                    System.out.println(broker.host());
//                    tp.put(new TopicAndPartition(tm.topic(), pm.partitionId()),
//                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));
//                }
//            }
//        }
//
//        OffsetRequest off = new OffsetRequest(tp, kafka.api.OffsetRequest.CurrentVersion(), "leaderLookup");
//        OffsetResponse offsetsBefore = consumer.getOffsetsBefore(off);
//        for (TopicAndPartition tps : tp.keySet()) {
//            long[] offsets = offsetsBefore.offsets(tps.topic(), tps.partition());
//            for (long offset : offsets) {
//                System.out.println(tps.topic() + ":" + tps.partition() + ":" + offset);
//            }
//        }


//            Map<TopicAndPartition, PartitionOffsetRequestInfo> m = new HashMap<>();
//            m.put(new TopicAndPartition("test-1",1),new PartitionOffsetRequestInfo(10000,1));
//            //OffsetRequest offr = new OffsetRequest(m,);
//            //consumer.getOffsetsBefore()
        System.out.println();
    }

    //获取topic的最大offset
    // TODO:为什么KafkaUtils.createDirectStream中不能用？
    public static Map<TopicAndPartition, Long> getLargstOffsets(Set<String> topics) {

        Map<TopicAndPartition, Long> map = new HashMap<>();

        TopicMetadataRequest tmr = new TopicMetadataRequest(new ArrayList<>(topics));

        SimpleConsumer consumer = new SimpleConsumer("vm-centos-00", 9092, 100000, 64 * 1024, "leaderLookup");
        TopicMetadataResponse msg = consumer.send(tmr);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> tp = new HashMap<>();


        for (TopicMetadata tm : msg.topicsMetadata()) {
            //System.out.println(tm.topic());
            for (PartitionMetadata pm : tm.partitionsMetadata()) {
                //System.out.println(pm.partitionId());
                for (Broker broker : pm.replicas()) {
                    //System.out.println(broker.host());
                    //long t = System.currentTimeMillis();
                    //System.out.println(t);
                    tp.put(new TopicAndPartition(tm.topic(), pm.partitionId()),
                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1));  //  1472008874815L
                }
            }
        }

        OffsetRequest off = new OffsetRequest(tp, kafka.api.OffsetRequest.CurrentVersion(), "leaderLookup");
        OffsetResponse offsetsBefore = consumer.getOffsetsBefore(off);
        for (TopicAndPartition tps : tp.keySet()) {
            long[] offsets = offsetsBefore.offsets(tps.topic(), tps.partition());
            for (long offset : offsets) {
                //System.out.println(tps.topic() + ":" + tps.partition() + ":" + offset);
                map.put(new TopicAndPartition(tps.topic(),tps.partition()),offset);
            }
        }
        return map;
    }

    //从zk中返回offset


//看实际应用选择
//从redis或mysql中返回保存的offset


//最小的就不需要了吧


}
