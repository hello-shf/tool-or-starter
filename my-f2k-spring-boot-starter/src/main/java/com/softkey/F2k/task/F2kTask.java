package com.softkey.F2k.task;

import com.softkey.F2k.common.F2kDictionary;
import com.softkey.F2k.common.F2kException;
import com.softkey.F2k.common.F2kStatus;
import com.softkey.F2k.common.F2kStatusEnum;
import com.softkey.F2k.properties.F2kProperties;
import com.softkey.F2k.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述：定时任务，校验加密狗
 *
 * @Author shf
 * @Date 2019/4/16 9:51
 * @Version V1.0
 **/
@Slf4j
@Component
@EnableScheduling
@EnableConfigurationProperties(F2kProperties.class)
public class F2kTask {
    @Autowired
    private F2kProperties f2kProperties;

//    @Scheduled(cron = "0/5 * * * * ?")//5秒执行一次
    @Scheduled(fixedRate = 1000*60*1)//启动校验一次，之后每隔1分钟校验一次
    public void checkF2kLock(){
        String devicePath = F2kUtil.findLockPath();
        if(null == devicePath || devicePath.isEmpty()){
            F2kStatus.setF2kstatus(F2kStatusEnum.NOTFOUNDLOCK);
            log.error("未找到加密锁，请插入加密锁后，再进行操作！！！");
            return;
        }
        log.info("找到加密锁！！！");
        int ret = F2kUtil.checkKeyByEncstring(f2kProperties.getInStr(), f2kProperties.getOutStr(), devicePath);
        if (ret != 0) {
            F2kStatus.setF2kstatus(F2kStatusEnum.NOTRIGHTLOCK);
            log.error("未能找到指定的加密锁！！！");
            return;
        }
        log.info("找到指定的加密锁！！！");
        String expirationDate = F2kUtil.readString(
                F2kDictionary.EXPIRATION_DATE_ADDRESS,
                F2kDictionary.EXPIRATION_DATE_LENGTH,
                f2kProperties.getRKeyH(),
                f2kProperties.getRKeyL(),
                devicePath);
        if(null == expirationDate || expirationDate.isEmpty()){
            try {
                log.error("加密锁读密码错误！！！");
                throw new F2kException("加密锁读密码错误！！！");
            } catch (F2kException e) {
                e.printStackTrace();
            }
        }
        String nowDate = F2kUtil.getNowDate();
        if(Integer.valueOf(nowDate) > Integer.valueOf(expirationDate)){
            F2kStatus.setF2kstatus(F2kStatusEnum.OVEREXPIRATION);
            log.error("系统使用时间到期，请联系感知科技！！！");
            return;
        }
        log.info("定时任务校验加密锁完成！！！");
        F2kStatus.setF2kstatus(F2kStatusEnum.OK);
    }
    /*public void checkF2kLock(){
        String devicePath = F2kUtil.findLockPath();
        if(null == devicePath || devicePath.isEmpty()){
            F2kStatus.setF2kstatus(F2kStatusEnum.NOTFOUNDLOCK);
            log.error("未找到加密锁，请插入加密锁后，再进行操作！！！");
            return;
        }

//        boolean isMatch = F2kUtil.matchLock(devicePath, f2kProperties.getKeyId());
//        if(!isMatch){
//            F2kStatus.setF2kstatus(F2kStatusEnum.NOTRIGHTLOCK);
//            log.error("未找到指定的加密锁！！！");
//            return;
//        }
        boolean isVerify = F2kUtil.verifyPublicKey(f2kProperties.getPubKeyX(), f2kProperties.getPubKeyY(), f2kProperties.getPin(), devicePath);
        if(!isVerify){
            try {
                log.error("加密锁公钥匹配失败！！！");
                throw new F2kException("加密锁公钥匹配失败！！！");
            } catch (F2kException e) {
                e.printStackTrace();
            }
        }
        String expirationDate = F2kUtil.readString(
                F2kDictionary.EXPIRATION_DATE_ADDRESS,
                F2kDictionary.EXPIRATION_DATE_LENGTH,
                f2kProperties.getRKeyH(),
                f2kProperties.getRKeyL(),
                devicePath);
        if(null == expirationDate || expirationDate.isEmpty()){
            try {
                log.error("加密锁读密码错误！！！");
                throw new F2kException("加密锁读密码错误！！！");
            } catch (F2kException e) {
                e.printStackTrace();
            }
        }
        String nowDate = F2kUtil.getNowDate();
        if(Integer.valueOf(nowDate) > Integer.valueOf(expirationDate)){
            F2kStatus.setF2kstatus(F2kStatusEnum.OVEREXPIRATION);
            log.error("系统使用时间到期，请联系感知科技！！！");
            return;
        }
        log.info("定时任务校验加密锁完成！！！");
        F2kStatus.setF2kstatus(F2kStatusEnum.OK);
    }*/
}
