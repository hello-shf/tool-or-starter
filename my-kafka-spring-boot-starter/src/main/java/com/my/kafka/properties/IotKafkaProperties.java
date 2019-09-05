package com.my.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "iot-kafka")
@Data
public class IotKafkaProperties {
    /**
     * kafka节点
     */
    private String brokers;
    /**
     * zk节点
     */
    private String zkNodes;
    /**
     * 消息组
     */
    private String group="defaultGroup";
    /**
     *
     */
    private int batchSize=655360;
    /**
     *
     */
    private int lingerMs=1;
    /**
     *
     */
    private int bufferMemory=524288;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

}
