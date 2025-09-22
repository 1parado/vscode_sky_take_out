package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sky.enumeration.OperationType;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill { // @interface 创建自定义注解
    // 对于数据库的修改 新增的操作
    OperationType value();
}
