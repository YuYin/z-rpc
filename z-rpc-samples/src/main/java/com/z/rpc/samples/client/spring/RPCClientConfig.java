/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client.spring;

import com.z.rpc.client.AbstractRpcClient;
import com.z.rpc.client.RpcClient;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.zookeeper.ZookeeperRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
@Configuration
public class RPCClientConfig {
    String registryAddress = "192.168.34.254:2181";


    @Bean(value = Constants.REGISTRY_BEAN_NAME)
    public Registry registry() {
        return new ZookeeperRegistry(registryAddress);
    }

    @Bean(value = Constants.CLIENT_BEAN_NAME,initMethod = "startup")
    public AbstractRpcClient rpcClient(Registry registry) {
        return new RpcClient(registry, RpcSerializerProtocol.HESSIANSERIALIZE);
    }
}
