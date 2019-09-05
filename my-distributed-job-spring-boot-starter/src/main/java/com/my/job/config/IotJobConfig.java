package com.my.job.config;

import com.my.job.properties.IotJobProperties;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
@EnableConfigurationProperties(IotJobProperties.class)//开启使用映射实体对象
public class IotJobConfig {
    @Autowired
    IotJobProperties iotJobProperties;
    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> iot config init.");
        //XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        XxlJobSpringExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setAdminAddresses(iotJobProperties.getAdminAddresses());
        xxlJobExecutor.setAppName(iotJobProperties.getName());
        xxlJobExecutor.setIp(iotJobProperties.getIp());
        xxlJobExecutor.setPort(iotJobProperties.getPort());
        xxlJobExecutor.setAccessToken(iotJobProperties.getAccessToken());
        xxlJobExecutor.setLogPath(iotJobProperties.getLogpath());
        xxlJobExecutor.setLogRetentionDays(iotJobProperties.getLogretentiondays());
        return xxlJobExecutor;
    }
}
