/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.loadbalance;

import com.z.rpc.registry.ServerNode;

import java.util.List;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public abstract class AbstractLoadBalance {

   public    abstract  String route(List<ServerNode> serverList);
}
