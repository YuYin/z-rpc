package com.z.rpc.client.hystrix.config;

import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * 线程池参数相关配置生成
 */
public class HystrixThreadPoolPropertiesFactory {

    public static HystrixThreadPoolProperties.Setter create() {
        return HystrixThreadPoolProperties.Setter().withCoreSize(10)
                .withAllowMaximumSizeToDivergeFromCoreSize(true)
                .withMaximumSize(20)
                .withMaxQueueSize(-1)
                .withKeepAliveTimeMinutes(1);
    }
}
