/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public enum LoadBalanceStrategy {

    RANDOM(new RandomLoadBalance()),
    ROUND_ROBIN(new RoundRobinLoadBalance()),

    IP_HASH(new IpHashLoadBalance()),
    WEIGHT_ROUND_ROBIN(new SmoothWeightedRoundRobin());

    public AbstractLoadBalance loadBalance;

    LoadBalanceStrategy(AbstractLoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

}
