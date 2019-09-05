package com.my.fastdfs.config;

import com.my.fastdfs.core.IotFastdfsConnectionPool;
import com.my.fastdfs.properties.IotFastdfsProperties;
import com.my.fastdfs.service.IotFastdfsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;



@Configuration
@EnableConfigurationProperties(IotFastdfsProperties.class)//开启使用映射实体对象
public class IotFastdfsConfiguration {
    @Autowired
    private IotFastdfsProperties iotFastdfsProperties;

    /**
     * 初始化连接池
     * @return
     */
    @Bean
    public IotFastdfsConnectionPool iotFastdfsConnectionPool(){
        return new IotFastdfsConnectionPool(iotFastdfsProperties);
    }

    /**
     * 初始化操作类
     * @return
     */
    @Bean
    public IotFastdfsTemplate iotFastdfsTemplate(IotFastdfsConnectionPool iotFastdfsConnectionPool) throws IOException {
        return new IotFastdfsTemplate(iotFastdfsConnectionPool);
    }
}
