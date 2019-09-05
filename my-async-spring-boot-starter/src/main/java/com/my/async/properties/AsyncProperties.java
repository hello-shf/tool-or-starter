package com.my.async.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;


@ConfigurationProperties(prefix = "iot-async")
@Data
public class AsyncProperties {
    List<AsyncEntity> list =new ArrayList<>();
}
