package com.gy.gyeway.rpc.annotation;

import java.lang.annotation.*;

/**
 * Rpc服务注解
 * @Description:
 * @author  zk
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface RPCService {
    String value() default "";

}