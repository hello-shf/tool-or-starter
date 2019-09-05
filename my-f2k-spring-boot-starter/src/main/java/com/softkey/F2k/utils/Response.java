package com.softkey.F2k.utils;

import lombok.Data;

/**
 * 描述：
 *
 * @Author shf
 * @Description TODO
 * @Date 2019/4/16 15:03
 * @Version V1.0
 **/
@Data
public class Response {
    private String code;
    private String msg;
    private Object data;
    public Response() {
        this.code = "200";
        this.msg = "OK";
    }
    public Response(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public void buildSuccessResponse(){
        this.code = "200";
        this.msg = "OK";
    }
    public void buildFailedResponse(){
        this.code = "-120";
        this.msg = "FAILED";
    }
    public void buildFailedResponse(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
/**
 * 错误码解释：
 *  -120：服务端加密锁不存在
 *  -121：服务端加密锁校验失败
 *  -122：服务端加密锁过期
 */

