package com.softkey.F2k.utils;

import com.softkey.F2k.config.F2kWebAdapterConfig;
import com.softkey.F2k.task.F2kTask;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({F2kWebAdapterConfig.class, F2kTask.class})
@Documented
@Inherited
public @interface EnableF2kValidator {
    String value() default "";
    Class<?>[] exclude() default {};
}
