package com.my.dynamic.utils.aop;

import com.my.dynamic.utils.anno.DataSource;
import com.my.dynamic.utils.DynamicDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 描述：动态数据源切面
 * @author: shf
 * @date: 2018-05-11 10:43:42
 * @version: V1.0
 */
@Aspect
@Component
public class DataSourceAspect {

    /**
     * 使用空方法定义切点表达式
     */
    @Pointcut("@annotation(com.my.dynamic.utils.anno.DataSource)")
//    @Pointcut("@within(DataSource)")
    public void methodPointCut() {
        System.out.println("成功进入切点");
    }
    /**
     * 使用环绕切入方式切入
     * @param point
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Around("methodPointCut()&&@annotation(dataSource)")
    public Object around(ProceedingJoinPoint point, DataSource dataSource) throws Exception {
        String db = dataSource.value();
        if("".equals(db)){
            throw new Exception("请在DataSource注解中添加数据源名称");
        }
        DynamicDataSource.setDataSource(db);
        try {
            return point.proceed();//不执行proceed方法，切入点方法就不会执行
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}