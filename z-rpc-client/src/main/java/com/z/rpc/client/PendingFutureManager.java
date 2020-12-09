/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/6
 */
public class PendingFutureManager {

        public static ConcurrentHashMap<String, InvokeFuture> pendingRPC = new ConcurrentHashMap<>();
}
