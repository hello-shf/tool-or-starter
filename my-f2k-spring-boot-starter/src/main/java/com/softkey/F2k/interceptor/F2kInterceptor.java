package com.softkey.F2k.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.softkey.F2k.common.F2kStatus;
import com.softkey.F2k.common.F2kStatusEnum;
import com.softkey.F2k.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 *  描述：拦截器，如果加密锁校验失败，拦截请求
 * @Author shf
 * @Date 2019/4/15 10:15
 * @Version V1.0
 **/
@Slf4j
public class F2kInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果不是OK状态，要拿到response的writer，会造成controller中的response响应无效，所以要分开处理
        if (F2kStatus.getF2kStatus().equals(F2kStatusEnum.OK)) {
            return true;
        } else {//加密锁异常状态，直接处理请求
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            Response rs = new Response();
            String jsonObject = "";
            switch (F2kStatus.getF2kStatus()) {
                case NOTFOUNDLOCK:
                    rs.buildFailedResponse("-120","未找到加密锁，请插入加密锁后，再进行操作！！！");
                    jsonObject = JSONObject.toJSONString(rs);
                    writer.print(jsonObject);
                    writer.flush();
                    return false;
                case NOTRIGHTLOCK:
                    rs.buildFailedResponse("-121", "未找到指定的加密锁！！！");
                    jsonObject = JSONObject.toJSONString(rs);
                    writer.print(jsonObject);
                    writer.flush();
                    return false;
                case OVEREXPIRATION:
                    rs.buildFailedResponse("-122", "系统使用时间到期，请联系感知科技！！！");
                    jsonObject = JSONObject.toJSONString(rs);
                    writer.print(jsonObject);
                    writer.flush();
                    return false;
                default:
                    return true;
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}
