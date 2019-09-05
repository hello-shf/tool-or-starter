package com.my.kafka.config;

import com.my.kafka.properties.IotKafkaProperties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableConfigurationProperties(IotKafkaProperties.class)//开启使用映射实体对象
@EnableKafka
public class IotKafkaConfig {
    @Autowired
    IotKafkaProperties iotKafkaProperties;

    /**
     * kafka模板
     * @return
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<String, String>(producerFactory());
        return kafkaTemplate;
    }

    /**
     * 生产者工厂
     * @return
     */
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
        //指定kafka 代理地址，可以多个
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, iotKafkaProperties.getBrokers());
        //每次批量发送消息的数量
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536);
        //修改最大发送数位4M
        properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 4194304);
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 100000);

        //指定消息key和消息体的编解码方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        //用户认证
        properties.put(CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL,"SASL_PLAINTEXT");
        properties.put( CommonClientConfigs.SECURITY_PROTOCOL_DOC, "KafkaClient {\n" +
                "org.apache.kafka.common.security.plain.PlainLoginModule required\n" +
                "username=\""+iotKafkaProperties.getUsername()+"\"\n" +
                "password=\""+iotKafkaProperties.getPassword()+"\";\n" +
                "};\n");
        properties.put(SaslConfigs.DEFAULT_SASL_MECHANISM,"PLAIN");


        return new DefaultKafkaProducerFactory<String, String>(properties);
    }


    /**
     * 监听器工厂
     * @return
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(4);
        factory.getContainerProperties().setPollTimeout(4000);
        return factory;
    }

    /**
     * 消费者工厂
     * @return
     */
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, iotKafkaProperties.getBrokers());
        //指定默认消费者group id
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, iotKafkaProperties.getGroup());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        //用户认证
        properties.put(CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL,"SASL_PLAINTEXT");
        properties.put( CommonClientConfigs.SECURITY_PROTOCOL_DOC, "KafkaClient {\n" +
                "org.apache.kafka.common.security.plain.PlainLoginModule required\n" +
                "username=\""+iotKafkaProperties.getUsername()+"\"\n" +
                "password=\""+iotKafkaProperties.getPassword()+"\";\n" +
                "};\n");
        properties.put(SaslConfigs.DEFAULT_SASL_MECHANISM,"PLAIN");
        return new DefaultKafkaConsumerFactory<String, String>(properties);
    }
}
