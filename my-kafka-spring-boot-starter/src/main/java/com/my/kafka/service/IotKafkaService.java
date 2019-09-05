package com.my.kafka.service;

import lombok.Data;
import org.springframework.kafka.core.KafkaTemplate;


@Data
public class IotKafkaService<T,S> {
    private KafkaTemplate<T,S> kafkaTemplate;
    //发送消息方法
    public void send(String topic,S obj) {
        kafkaTemplate.send(topic, obj);
    }
}
