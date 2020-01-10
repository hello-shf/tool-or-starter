package com.my.dynamic.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "iot-datasource")
public class DynamicDataSourceProperties {
    private List<DynamicDataSourceEntity> db = new ArrayList<>();

    public List<DynamicDataSourceEntity> getDb() {
        return db;
    }

    public void setDb(List<DynamicDataSourceEntity> db) {
        this.db = db;
    }
}
