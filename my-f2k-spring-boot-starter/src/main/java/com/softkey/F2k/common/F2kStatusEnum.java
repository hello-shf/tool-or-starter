package com.softkey.F2k.common;

/**
 * 描述：加密锁校验结果枚举类
 *
 * @Author shf
 * @Description TODO
 * @Date 2019/4/16 10:22
 * @Version V1.0
 **/
public enum F2kStatusEnum {
    /**
     * 未发现加密锁
     */
    NOTFOUNDLOCK,
    /**
     * 不是指定的加密锁
     */
    NOTRIGHTLOCK,
    /**
     * 公钥错误
     */
    PUBKEYERROR,
    /**
     * 加密锁过期
     */
    OVEREXPIRATION,
    /**
     * 加密锁正常
     */
    OK
}
