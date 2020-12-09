package com.z.rpc.client.hystrix.config;

import com.netflix.hystrix.HystrixCommandProperties;
import com.z.rpc.common.RPCSystemConfig;

/**
 * 命令降级参数相关配置
 * 参数参考：
 * <a>http://moguhu.com/article/detail?articleId=81</a>
 * <a>https://github.com/yskgood/dubbo-hystrix-support<a/>
 */
public class HystrixCommandPropertiesFactory {

    public static HystrixCommandProperties.Setter create() {
        return HystrixCommandProperties.Setter()
                //熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
                .withCircuitBreakerSleepWindowInMilliseconds(RPCSystemConfig.HYSTRIX_SLEEP_WINDOW_IN_MILLISECONDS)
                //熔断触发后多久恢复half-open状态，熔断后sleepWindowInMilliseconds毫秒会放入一个请求，如果请求处理成功，熔断器关闭，否则熔断器打开，继续等待sleepWindowInMilliseconds
                .withCircuitBreakerErrorThresholdPercentage(RPCSystemConfig.HYSTRIX_ERROR_THRESHOLD_PERCENTAGE)
               //一个统计周期内（默认10秒）请求不少于requestVolumeThreshold才会进行熔断判断
                .withCircuitBreakerRequestVolumeThreshold(RPCSystemConfig.HYSTRIX_REQUEST_VOLUME_THRESHOLD)
                //当隔离策略为THREAD时，当线程执行超时，是否进行中断处理。（默认为true）这里指的是同步调用：execute()
                .withExecutionIsolationThreadInterruptOnTimeout(true)
                //任务执行超时时间，注意该时间和dubbo自己的超时时间不要冲突，以这个时间优先，比如consumer设置3秒，那么当执行时hystrix会提前超时 
                .withExecutionTimeoutInMilliseconds(RPCSystemConfig.HYSTRIX_TIMEOUT_IN_MILLISECONDS)
                //Fallback并发调用量控制
                .withFallbackIsolationSemaphoreMaxConcurrentRequests(RPCSystemConfig.HYSTRIX_FALLBACK_MAX_CONCURRENT_REQUESTS)
                //隔离策略,默认线程池隔离
                .withExecutionIsolationStrategy(IsolationStrategy.getIsolationStrategy())
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(RPCSystemConfig.HYSTRIX_MAX_CONCURRENT_REQUESTS);

    }
}
