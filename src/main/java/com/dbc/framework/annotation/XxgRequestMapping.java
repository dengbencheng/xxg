package com.dbc.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther dbc
 * @Date 2020/9/28 16:20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XxgRequestMapping {
    String value() default "";
    String[] methods() default {"GET", "POST", "PUT", "DELETE"};
}
