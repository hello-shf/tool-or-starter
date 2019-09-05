package com.my.mongo.config;

import com.my.mongo.properties.MongoOptionProperties;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnClass({MongoClient.class})
@EnableConfigurationProperties(MongoOptionProperties.class)
@ConditionalOnMissingBean(type = "org.springframework.data.mongodb.MongoDbFactory")
public class IotMongoAutoConfiguration {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public MongoClientOptions mongoClientOptions(MongoOptionProperties mongoOptionProperties) {
        if (mongoOptionProperties == null) {
            log.info("mongoOptionProperties is null!");
            return new MongoClientOptions.Builder().build();
        }
        log.info("Start the registration of mongo plug-in");
        return new MongoClientOptions.Builder()
                .minConnectionsPerHost(mongoOptionProperties.getMinConnectionPerHost())
                .connectionsPerHost(mongoOptionProperties.getMaxConnectionPerHost())
                .threadsAllowedToBlockForConnectionMultiplier(mongoOptionProperties.getThreadsAllowedToBlockForConnectionMultiplier())
                .serverSelectionTimeout(mongoOptionProperties.getServerSelectionTimeout())
                .maxWaitTime(mongoOptionProperties.getMaxWaitTime())
                .maxConnectionIdleTime(mongoOptionProperties.getMaxConnectionIdleTime())
                .maxConnectionLifeTime(mongoOptionProperties.getMaxConnectionLifeTime())
                .connectTimeout(mongoOptionProperties.getConnectTimeout())
                .socketTimeout(mongoOptionProperties.getSocketTimeout())
                .socketKeepAlive(mongoOptionProperties.getSocketKeepAlive())
                .sslEnabled(mongoOptionProperties.getSslEnabled())
                .sslInvalidHostNameAllowed(mongoOptionProperties.getSslInvalidHostNameAllowed())
                .alwaysUseMBeans(mongoOptionProperties.getAlwaysUseMBeans())
                .heartbeatFrequency(mongoOptionProperties.getHeartbeatFrequency())
                .minConnectionsPerHost(mongoOptionProperties.getMinConnectionPerHost())
                .heartbeatConnectTimeout(mongoOptionProperties.getHeartbeatConnectTimeout())
                .heartbeatSocketTimeout(mongoOptionProperties.getSocketTimeout())
                .localThreshold(mongoOptionProperties.getLocalThreshold())
                .build();
    }
}