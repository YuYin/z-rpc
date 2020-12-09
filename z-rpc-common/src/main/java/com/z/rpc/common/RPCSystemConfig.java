/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/22
 */
public class RPCSystemConfig {

    public static final String SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR = "zrpc.parallel.rejected.policy";
    public static final String SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR = "zrpc.parallel.queue";
    public static final int SYSTEM_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());
    public static final int SYSTEM_PROPERTY_THREADPOOL_THREAD_NUMS = Integer.getInteger("zrpc.default.thread.nums", 16);
    public static final int SYSTEM_PROPERTY_THREADPOOL_QUEUE_NUMS = Integer.getInteger("zrpc.default.queue.nums", 65535);

    public static final int SERIALIZE_POOL_MAX_TOTAL = 500;
    public static final int SERIALIZE_POOL_MIN_IDLE = 10;
    public static final int SERIALIZE_POOL_MAX_WAIT_MILLIS = 5000;
    public static final int SERIALIZE_POOL_MIN_EVICTABLE_IDLE_TIME_MILLIS = 600000;
    //同步调用超时时间
    public static final long SYSTEM_PROPERTY_SYNC_CALL_TIMEOUT = Long.getLong("zrpc.default.msg.timeout", 30 * 1000L);

    public static final long CONNECTION_TIMEOUT = Long.getLong("zrpc.client.connection.timeout", 5 * 1000L);

    public static final int HYSTRIX_SLEEP_WINDOW_IN_MILLISECONDS = Integer.getInteger("zrpc.hystrix.sleepWindowInMilliseconds", 5000);
    public static final int HYSTRIX_ERROR_THRESHOLD_PERCENTAGE = Integer.getInteger("zrpc.hystrix.errorThresholdPercentage", 50);

    public static final int HYSTRIX_REQUEST_VOLUME_THRESHOLD = Integer.getInteger("zrpc.hystrix.requestVolumeThreshold", 20);

    public static final int HYSTRIX_TIMEOUT_IN_MILLISECONDS = Integer.getInteger("zrpc.hystrix.timeoutInMilliseconds", 30000);
    public static final int HYSTRIX_FALLBACK_MAX_CONCURRENT_REQUESTS = Integer.getInteger("zrpc.hystrix.fallbackMaxConcurrentRequests", 1000);
    public static final int HYSTRIX_MAX_CONCURRENT_REQUESTS = Integer.getInteger("zrpc.hystrix.maxConcurrentRequests", 10);

    public static final String ENABLE_HYSTRIX_KEY="enableHystrixKey";

    public static final String FALL_BACK_CLASS_KEY="fallBackClassKey";

    public static final IsolationType ISOLATION_TYPE = IsolationType.THREAD;

    public enum IsolationType {
        THREAD,
        SEMAPHORE;
    }


}
