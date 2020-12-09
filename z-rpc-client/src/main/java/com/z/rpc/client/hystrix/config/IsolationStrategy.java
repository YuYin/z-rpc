package com.z.rpc.client.hystrix.config;

import com.netflix.hystrix.HystrixCommandProperties;
import com.z.rpc.common.RPCSystemConfig;

/**
 * 隔离策略
 */
public class IsolationStrategy {


    /**
     * 获取隔离策略，默认使用线程池
     *
     * @param url
     * @return
     */
    public static HystrixCommandProperties.ExecutionIsolationStrategy getIsolationStrategy() {
        if (RPCSystemConfig.ISOLATION_TYPE== RPCSystemConfig.IsolationType.THREAD) {
            return HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;
        } else {
            return HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE;
        }
    }
}
