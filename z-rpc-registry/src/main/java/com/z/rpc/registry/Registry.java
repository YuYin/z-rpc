/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.registry;

import org.apache.zookeeper.KeeperException;

import java.util.List;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/23
 */
public interface Registry {

     String register(ServerNode serverNode);

     void unregister(String path) throws KeeperException, InterruptedException;

      List<ServerNode> subscribe(DataChangeCallBack changeCallBack);
}
