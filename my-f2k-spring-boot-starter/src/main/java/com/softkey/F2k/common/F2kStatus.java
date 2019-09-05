package com.softkey.F2k.common;


/**
 * 描述：记录F2k的状态
 *
 * @Author shf
 * @Description TODO
 * @Date 2019/4/16 9:42
 * @Version V1.0
 **/
public class F2kStatus {
    public static F2kStatusEnum f2kStatus = F2kStatusEnum.NOTFOUNDLOCK;

    public static F2kStatusEnum getF2kStatus() {
        return f2kStatus;
    }

    public static void setF2kstatus(F2kStatusEnum f2kstatus) {
        f2kStatus = f2kstatus;
    }
}
