package com.my.ftp.properties;

import lombok.Data;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot-ftp")
@Data
public class FtpOptionProperties {
    private String host;
    private int port= FTPClient.DEFAULT_PORT;
    private String username;
    private String password;
    private int bufferSize = 8096;
    /**
     * 初始化连接数
     */
    private Integer initialSize = 0;
    private String encoding;


}