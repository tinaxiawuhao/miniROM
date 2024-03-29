package com.test.miniorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//该注解用来设置注解
@Retention(RetentionPolicy.RUNTIME) //运行期间保留注解的信息
@Target(ElementType.FIELD) //设置注解用到什么地方
public @interface ORMId {
}
