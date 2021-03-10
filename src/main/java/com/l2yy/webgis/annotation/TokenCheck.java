package com.l2yy.webgis.annotation;

import java.lang.annotation.*;

/**
 * @author ：hjl
 * @date ：Created in 2020/4/11 2:16 下午
 * @description：
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenCheck {
    String value() default "";
}
