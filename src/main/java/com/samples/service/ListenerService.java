package com.samples.service;

import com.samples.config.KafkaConfig;
import com.samples.consumer.CSVConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by I681336 on 8/30/2017.
 */
@Component
public class ListenerService {

    public static final Logger log = LoggerFactory.getLogger(ListenerService.class);
    @Autowired
    public SenderService senderService;
    @Autowired
    public CSVConsumer csvConsumer;
    @Autowired
    public KafkaConfig kafkaConfig;

    @KafkaListener(topics={"${topic.sourceName}"})
    public void listenCSV(ConsumerRecord<String,String> record)  {
        try {
            Future<Boolean> result = csvConsumer.consumeMsg(record);
            result.get(kafkaConfig.timeout-1000, TimeUnit.MILLISECONDS); // to handle messages that take too much time.
        }catch(Exception kse) {
            log.error("Exception while processing record {}. Message: {}",record.key(),kse.getMessage());
            kse.printStackTrace();
            try {
                senderService.sendException(kafkaConfig.exceptionsTopic,record.key(),record.value());
            } catch (Exception e) {
                log.error("Unable to send Exception to Exceptions Topic");
                log.error("Key:"+record.key()+" \n Value: "+record.value());
                e.printStackTrace();
            }
        }

    }

}
