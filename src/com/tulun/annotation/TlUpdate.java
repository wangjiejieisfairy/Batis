package com.tulun.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 描述: 实现框架的update注解
 *
 * @Author shilei
 * @Date 2018/11/11
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TlUpdate {
    String value();
}
