/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client;

import com.google.auto.service.AutoService;
import com.z.rpc.client.hystrix.Fallback;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/9/8
 */
@AutoService(Fallback.class)
public class HelloServiceFallback implements Fallback {
    @Override
    public String invoke() {
        return "xxxxxxxxxxxxxxxxxxxxx";
    }
}
