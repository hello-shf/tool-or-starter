package com.softkey.F2k.common;

/**
 *  描述：定义F2K异常
 * @Author shf
 * @Description TODO
 * @Date 2019/4/15 15:11
 * @Version V1.0
 **/
public class F2kException extends Exception {
    String errorType;
    public F2kException(){
        super();
    }
    public F2kException(String message){
        super(message);
    }
    public F2kException(String message, String errorType){
        super(message);
        this.errorType = errorType;
    }
    public String getErrorType(){
        return errorType;
    }
}
