/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client;

import com.z.rpc.client.connection.Connection;
import com.z.rpc.client.loadbalance.LoadBalanceStrategy;
import com.z.rpc.client.proxy.ClientProxy;
import com.z.rpc.common.AbstractLifeCycle;
import com.z.rpc.common.Constants;
import com.z.rpc.common.exception.LifeCycleException;
import com.z.rpc.common.filter.FilterBuilder;
import com.z.rpc.common.filter.FilterBuilderBase;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.config.ConsumerConfig;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.ServerNode;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/28
 */
public abstract class AbstractRpcClient extends AbstractLifeCycle {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRpcClient.class);


    protected Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    protected static NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
    protected Registry registry;
    protected RpcSerializerProtocol rpcSerializerProtocol;
    protected List<ServerNode> serverNodeList = new ArrayList<>();
    private FilterBuilder filterBuilder=new FilterBuilderBase(Constants.CONSUMER);
    protected Map<String,AsyncRPCCallback> methodCallbackMap=new HashMap<>();

    public AbstractRpcClient(Registry registry, RpcSerializerProtocol rpcSerializerProtocol) {
        this.registry = registry;
        this.rpcSerializerProtocol = rpcSerializerProtocol;
    }

    public void startup() throws Exception {
        try {
            super.startup();
            if (doStart()) {
                logger.info("客户端启动成功");
            }
        } catch (Throwable t) {
            shutdown();
            throw new IllegalStateException(t);
        }
    }

    protected abstract boolean doStart() throws Exception;

    protected abstract boolean doStop() throws Exception;



    public void shutdown() throws Exception {
        super.shutdown();
        if (!doStop()) {
            throw new LifeCycleException("client  doStop fail");
        }
    }

    public Connection getConnection(String serverAddress) {
        return connectionMap.get(serverAddress);
    }

    public List<ServerNode> getServerNodeList() {
        return serverNodeList;
    }

    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T> interfaceClass, ConsumerConfig consumerConfig, LoadBalanceStrategy loadBalanceStrategy) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ClientProxy<T>(consumerConfig, loadBalanceStrategy, this)
        );
    }

    public <T> T proxy(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ClientProxy<T>(this,filterBuilder)
        );
    }
        public <T> T proxy(Class<T> interfaceClass,ConsumerConfig consumerConfig) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ClientProxy<T>(this,consumerConfig,filterBuilder)
        );
    }

    public void registerCallback(String key,AsyncRPCCallback asyncRPCCallback){
        methodCallbackMap.putIfAbsent(key,asyncRPCCallback);
    }
    public AsyncRPCCallback getMethodCallback(String key){
       return methodCallbackMap.get(key);
    }

}
