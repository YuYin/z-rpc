/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client;

import com.z.rpc.client.loadbalance.LoadBalanceStrategy;
import com.z.rpc.common.CallType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    long timeout()default 30*1000L;

    String directUrl() default "";
    
    CallType callType()default CallType.SYNC;

    int retries() default 0;

    LoadBalanceStrategy loadBalance()default LoadBalanceStrategy.WEIGHT_ROUND_ROBIN;

    boolean enableHystrix()default false;

    String  fallBackClassName() default "";
}
