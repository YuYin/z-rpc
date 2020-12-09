/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.registry.redis;

import com.alibaba.fastjson.JSONObject;
import com.z.rpc.registry.ServerNode;
import com.z.rpc.registry.redis.client.event.RedisListener;
import com.z.rpc.registry.Constant;
import com.z.rpc.registry.DataChangeCallBack;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.redis.client.core.RedisClient;
import com.z.rpc.registry.redis.client.core.RedisClientFactory;
import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/24
 *         redis注册中心，发布订阅
 */
public class RedisRegistry implements Registry {
    @Override
    public String register(ServerNode serverNode) {
        RedisClient redisClient = RedisClientFactory.getClient();
        Map<String, ServerNode> map = new HashMap<>();
        map.put(serverNode.getAddress(), serverNode);
        redisClient.hmset(Constant.REDIS_REGISTRY_SERVICE_KEY, map);
        return serverNode.getAddress();
    }

    @Override
    public void unregister(String serverAddress) throws KeeperException, InterruptedException {
        RedisClient redisClient = RedisClientFactory.getClient();
        Map<String, ServerNode> map = redisClient.hgetAll(Constant.REDIS_REGISTRY_SERVICE_KEY);
        map.remove(serverAddress);
        redisClient.hmset(Constant.REDIS_REGISTRY_SERVICE_KEY, map);
        redisClient.publish(Constant.REDIS_SERVICE_CHANGE_CHANNEL, JSONObject.toJSONString(map));
    }


    @Override
    public List<ServerNode> subscribe(DataChangeCallBack changeCallBack) {
        RedisClient redisClient = RedisClientFactory.getClient();
        redisClient.subscribe(Constant.REDIS_SERVICE_CHANGE_CHANNEL, new RedisListener() {
            @Override
            public void onMessage(String channel, String message) {
                Map<String, ServerNode> map = (Map<String, ServerNode>) JSONObject.parseObject(message, Map.class);
                changeCallBack.call(new ArrayList<>(map.values()));
            }
        });
        Map<String, ServerNode> map = redisClient.hgetAll(Constant.REDIS_REGISTRY_SERVICE_KEY);
        if (map == null || map.size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(map.values());
    }
}
