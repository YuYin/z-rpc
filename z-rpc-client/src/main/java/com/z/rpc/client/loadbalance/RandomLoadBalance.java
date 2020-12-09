/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

import com.z.rpc.registry.ServerNode;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private Random random = new Random();

    @Override
    public String route(List<ServerNode> serverList) {
        List<String> list = serverList.stream().map(serverNode -> serverNode.getAddress()).collect(Collectors.toList());

        // arr
        String[] addressArr = list.toArray(new String[list.size()]);

        // random
        String finalAddress = addressArr[random.nextInt(list.size())];
        return finalAddress;
    }
}
