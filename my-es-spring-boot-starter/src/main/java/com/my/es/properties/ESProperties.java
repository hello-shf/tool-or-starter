package com.my.es.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "iot-es")
@Data
public class ESProperties {
    private String clusterName;
    private String poolSize="5";
    private String clusterNodes;
    private XPack xPack;



    @Data
    public static class XPack{
        private String user;
        private String password;
        private String sslEnabled="false";
        private String verificationMode;
        private String keystorePath;
        private String truststorePath;
    }

}
