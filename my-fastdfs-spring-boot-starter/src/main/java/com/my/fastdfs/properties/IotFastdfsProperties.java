package com.my.fastdfs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "iot-fastdfs")
public class IotFastdfsProperties {
    /**
     * 连接超时时间
     */
    private String connectTimeout="2000";

    /**
     * 网络超时时间
     */
    private String networkTimeout="30000";

    /**
     * 编码方式
     */
    private String charset="UTF-8";

    /**
     * tracker端口号
     */
    //private String httpTrackerHttpPort = "8080";

    /**
     * 是否使用token
     */
    private String httpAntiStealToken = "no";
    /**
     * secret key to generate anti-steal token
     * this parameter must be set when http.anti_steal.check_token set to true
     * the length of the secret key should not exceed 128 bytes
     */
    private String httpSecretKey;
    /**
     * tracker服务列表
     */
    private String trackerServers = "127.0.0.1:22122";

    private String maxSize="50";

    private String coreSize="20";
    private int retryTimes = 5;
}
