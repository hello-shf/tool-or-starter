package com.softkey.F2k.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * 描述：加密锁常规配置
 *
 * @Author shf
 * @Description TODO
 * @Date 2019/4/16 13:54
 * @Version V1.0
 **/
@Data
@ConfigurationProperties(prefix = "iot-f2k")
public class F2kProperties {
    private String keyId;
    private String pin;
    private String pubKeyX;
    private String pubKeyY;
    private String rKeyH = "FFFFFFFF";
    private String rKeyL = "FFFFFFFF";
    private String inStr = "BF1065FEEF828071616D6973756F00";
    private String outStr = "16AED81265CBC46C30D378849300F43E6ED4277B7C8157E437353646303000";
}
