package ir.invoice.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by I681336 on 8/30/2017.
 */
@Configuration
@EnableKafka
@EnableAsync
public class KafkaConfig {

    @Value("${broker.servers}")
    public String brokerServers;
    @Value("${topic.consumer.groupId}")
    public String consumerGroupId;
    @Value("${topic.consumer.count}")
    public int consumerCount;
    @Value("${topic.sourceName}")
    public String csvTopic;
    @Value("${topic.targetName}")
    public String jsonTopic;
    @Value("${topic.exceptionsTopicName}")
    public String exceptionsTopic;
    @Value("${topic.consumer.timeoutMs}")
    public int timeout=5000;

    @PostConstruct
    public void init() {
        timeout = timeout > 1000 ? timeout: 5000;
    }

    @Bean
    public ProducerFactory<String,String> producerFactory(){
        Map<String,Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerServers);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG,2);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,1);
        properties.put(ProducerConfig.LINGER_MS_CONFIG,100);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,10000000);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        return new DefaultKafkaProducerFactory<String, String>(properties);
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(){
        return new KafkaTemplate<String, String>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactory() {
        Map<String,Object> properties = new HashMap<>();
       properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerServers);
       properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
       properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);
       properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
       properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,timeout);
       properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,"org.apache.kafka.clients.consumer.RoundRobinAssignor");

       properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
       properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
       return new DefaultKafkaConsumerFactory<String, String>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(consumerCount);
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // keep both values same.
        executor.setMaxPoolSize(50);
        executor.setCorePoolSize(50);
        return executor;
    }

}
