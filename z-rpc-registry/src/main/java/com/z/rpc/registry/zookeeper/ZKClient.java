/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.registry.zookeeper;

import com.z.rpc.registry.Constant;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/27
 */
public class ZKClient {

    private static final Logger logger = LoggerFactory.getLogger(ZKClient.class);


    private ZooKeeper zookeeper;

    private CountDownLatch latch = new CountDownLatch(1);

    private static ZKClient zkClient;

    public static ZKClient getInstance() {
        if (zkClient == null) {
            synchronized (ZKClient.class) {
                if (zkClient == null) {
                    zkClient = new ZKClient();
                }
            }
        }
        return zkClient;
    }

    public ZooKeeper connectZK(String registryAddress) {
        try {
            zookeeper = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            logger.error("连接zookeeper失败", e);
        }
        return zookeeper;
    }

    public void close(){
        if(zookeeper!=null){
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                  logger.error("关闭zookeeper失败", e);
            }
        }
    }
}
