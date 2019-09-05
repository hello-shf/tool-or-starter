package com.my.dynamic.utils.anno;


import java.lang.annotation.*;

/**
 * 选定数据源注解
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSource {

    /**
     * value为数据源的dbname
     * @return
     */
    String value() default "";
}
