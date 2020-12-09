/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

import com.z.rpc.registry.ServerNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    Integer pos = 0;

    @Override
    public String route(List<ServerNode> serverList) {

        String address = null;
        List<String> list = serverList.stream().map(serverNode -> serverNode.getAddress()).collect(Collectors.toList());
        synchronized (pos) {
            if (pos >= serverList.size()) {
                pos = 0;
            }
            address = list.get(pos);
            //轮询+1
            pos++;
        }
        return address;
    }
}
