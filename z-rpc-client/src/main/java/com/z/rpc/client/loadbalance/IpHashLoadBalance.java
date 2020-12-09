/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

import com.z.rpc.common.utils.IpUtil;
import com.z.rpc.registry.ServerNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public class IpHashLoadBalance extends AbstractLoadBalance {
    @Override
    public String route(List<ServerNode> serverList) {
        List<String> list=serverList.stream().map(serverNode -> serverNode.getAddress()).collect(Collectors.toList());
        String clientIP = IpUtil.getIp();

        int pos = clientIP.hashCode() % serverList.size();
        return list.get(pos);
    }
}
