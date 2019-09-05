package com.softkey.F2k.utils;

import com.softkey.jsyunew6;
import com.softkey.sm2.SM2SM3;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author shf
 * @Description TODO
 * @Date 2019/4/15 14:08
 * @Version V1.0
 **/
@Slf4j
public class F2kUtil {
    /**
     * 获取加密锁的devicePath
     * @return devicePath:加密锁所在的设备路径 null:不存在加密锁
     */
    public static String findLockPath() {
        String devicePath = jsyunew6.FindPort(0);
        log.info("devicePath---->:{}", devicePath);
        if (jsyunew6.get_LastError() != 0) {
            return null;
        }
        return devicePath;
    }

    /**
     * 根据keyId校验是不是指定的加密锁
     * @param devicePath 加密锁所在的设备路径
     * @param keyId 加密锁keyId
     * @return false:不是指定的加密锁
     */
    public static boolean matchLock(String devicePath, String keyId) {
        int ID1, ID2;
        ID1 = jsyunew6.GetID_1(devicePath);
        log.info("ID1---->{}", ID1);
        if (jsyunew6.get_LastError() != 0) {
            return false;
        }
        ID2 = jsyunew6.GetID_2(devicePath);
        log.info("ID2---->{}", ID2);
        if (jsyunew6.get_LastError() != 0) {
            return false;
        }
        String keyid = Integer.toHexString(ID1) + Integer.toHexString(ID2);
        log.info("keyid---->{}", keyid);
        if(!keyid.equalsIgnoreCase(keyId)){
            return false;
        }
        return true;
    }

    /**
     * 校验公钥
     * @param pubKeyX 公钥X
     * @param pubKeyY 公钥Y
     * @param pin pin码
     * @param devicePath 加密锁所在的设备路径
     * @return
     */
    public static boolean verifyPublicKey(String pubKeyX, String pubKeyY, String pin, String devicePath) {
        String randomNum = new Random().nextInt(100000000)+"";
        String softEnc = SM2SM3.SM2_EncStringBySoft(randomNum,pubKeyX,pubKeyY);
        log.info("softEnc---->{}", softEnc);
        String outResult = jsyunew6.SM2_DecString(softEnc, pin, devicePath);
        log.info("outResult---->{}", outResult);
        if (jsyunew6.get_LastError() != 0) {
            log.info("SM2_DecString result---> {}", jsyunew6.get_LastError());
            return false;
        }
        if(!randomNum.equals(outResult)){
            return false;
        }
        return true;
    }

    /**
     * 向加密锁写入String类型数据
     * @param str 写入的内容
     * @param address 写入的位置  short类型
     * @param wKeyH 写密码高8位
     * @param wKeyL 写密码低8位
     * @param devicePath 加密锁所在的设备路径
     * @return null：写入失败
     */
    public static Integer writeString(String str, short address, String wKeyH, String wKeyL, String devicePath) {
        int length = jsyunew6.YWriteString(str, address, wKeyH, wKeyL, devicePath);
        if(jsyunew6.get_LastError() != 0){
            return null;
        }
        return length;
    }

    /**
     * 读取加密锁String类型数据
     * @param address 读取位置（对应写入时的address）  short类型
     * @param length 读取数据的长度（对应写入返回的长度） short类型
     * @param rKeyH 读密码高8位
     * @param rKeyL 读密码低8位
     * @param devicePath 加密锁所在的设备路径
     * @return null：读取失败
     */
    public static String readString(short address, short length, String rKeyH, String rKeyL, String devicePath) {
        log.info("执行读操作");
        String str = jsyunew6.YReadString(address, length, rKeyH, rKeyL, devicePath);
        log.info("读操作结果---->{}", str);
        if(jsyunew6.get_LastError() != 0){
            return null;
        }
        return str;
    }

    public static String getNowDate() {
        Date dateNow = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(dateNow);
    }

    public static String encString(String inString,String inPath){
        log.info("执行加密算法");
        String str = jsyunew6.SM2_EncString(inString, inPath);
        log.info("加密结果: {}", str);
        return str;
    }

    //增强算法加密一
    public static void encStringNew1(String devicePath){
        log.info("执行增强加密算法一");
        String encStr = jsyunew6.EncString("shf", devicePath);
        log.info("加密一结果：{}", encStr);
        String decStr = jsyunew6.DecString(encStr, "123456789987654321");
        log.info("解密一结果：{}", decStr);
    }
    //增强算法加密二
    public static void encStringNew2(String devicePath){
        log.info("执行增强加密算法二");
        String encStr = jsyunew6.EncString_New("shf", devicePath);
        log.info("加密二 结果：{}", encStr);
        String decStr = jsyunew6.DecString(encStr, "123456789987654321");
        log.info("解密二结果：{}", decStr);
    }

    /**
     * 使用增强加密算法二找指定加密狗
     * @param inStr
     * @param verifyStr
     * @return 1;//如果没有使用这个功能 2;//如果该锁不支持这个功能
     */
    public static int checkKeyByEncstring(String inStr, String verifyStr, String devicePath){
        log.info("通过增强算法二找加密狗，inStr={}", inStr);
        String outString = jsyunew6.EncString_New(inStr, devicePath);
        log.info("增强算法二，outString={}", outString);
        if ((jsyunew6.get_LastError() == 0) && (outString.compareTo(verifyStr) == 0)){
            return 0;
        }
        return -92;
    }
}
