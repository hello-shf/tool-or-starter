package com.my.job.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "iot-job")
@Data
public class IotJobProperties {
    /**
     * admin地址
     */
    private String adminAddresses;
    /**
     * 执行器名称
     */
    private String name;
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port=9999;
    /**
     * access token
     */
    private String accessToken;
    /**
     * 日志路径
     */
    private String logpath;
    /**
     * 保留天数
     */
    private int logretentiondays=-1;
}
