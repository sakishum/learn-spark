package com.asiainfo.common;


import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;

import java.util.*;

/**
 * Created by migle on 2016/8/23.
 */
public class KafkaTopicOffsetTool {
    public static void main(String[] args) {
        String[] topics = new String[]{"test-1","test-2","test3"};
        TopicMetadataRequest tmr = new TopicMetadataRequest(Arrays.asList(topics));
        SimpleConsumer consumer = new SimpleConsumer("vm-centos-00", 9092, 100000, 64 * 1024, "leaderLookup");
        TopicMetadataResponse msg = consumer.send(tmr);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> tp = new HashMap<>();


        for (TopicMetadata tm : msg.topicsMetadata()) {
            System.out.println(tm.topic());
            for (PartitionMetadata pm : tm.partitionsMetadata()) {
                System.out.println(pm.partitionId());
                for (Broker broker : pm.replicas()) {
                    System.out.println(broker.host());
                    tp.put(new TopicAndPartition(tm.topic(),pm.partitionId()),
                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(),1));
                }
            }

        }
//            Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
//            requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(OffsetRequest.LatestTime(), 1));
            OffsetRequest off = new OffsetRequest(tp,kafka.api.OffsetRequest.CurrentVersion(),"leaderLookup");
            OffsetResponse offsetsBefore = consumer.getOffsetsBefore(off);
            for (TopicAndPartition tps : tp.keySet()) {
                long[] offsets = offsetsBefore.offsets(tps.topic(), tps.partition());
                for (long offset : offsets){
                    System.out.println(tps.topic() + ":" + tps.partition() + ":" + offset);
                }
            }


//            Map<TopicAndPartition, PartitionOffsetRequestInfo> m = new HashMap<>();
//            m.put(new TopicAndPartition("test-1",1),new PartitionOffsetRequestInfo(10000,1));
//            //OffsetRequest offr = new OffsetRequest(m,);
//            //consumer.getOffsetsBefore()
            System.out.println();
    }


    public Map<TopicAndPartition, Long> getLargstOffsets(String hosts,Set<String> topics){
        Map<TopicAndPartition, Long> map = new TreeMap<>();


        return  map;
    }

}
