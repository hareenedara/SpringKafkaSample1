package com.samples.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by i681336 on 8/31/2017.
 */
@Component
public class SenderService {
    public static final Logger log = LoggerFactory.getLogger(SenderService.class);
    @Autowired
    KafkaTemplate kafkaTemplate;

    public void send(String topicName, String key,String val)  {
        kafkaTemplate.send(topicName,key,val);
        log.info(key+" -> "+val);

    }

    public void sendException(String topicName, String key, String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, key, msg);
        kafkaTemplate.send(topicName,key,msg);
        log.info("Sending Msg to Exception topic.");
    }
}
