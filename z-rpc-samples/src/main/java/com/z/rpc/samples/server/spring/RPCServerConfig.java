/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.server.spring;

import com.z.rpc.config.ProviderConfig;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.zookeeper.ZookeeperRegistry;
import com.z.rpc.server.AbstractRemoteServer;
import com.z.rpc.server.RPCServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
@Configuration
public class RPCServerConfig{
   // String serverAddress = "127.0.0.1:18866";
    String ip="127.0.0.1";
    int port=18866;
    Registry zookeeperRegistry = new ZookeeperRegistry("192.168.34.254:2181");
    String serviceScanPackages="com.z.rpc.samples";

    @Bean(value =Constants.SERVER_BEAN_NAME,initMethod = "startup")
    public AbstractRemoteServer rpcServer() {
        return new RPCServer(ip,port, zookeeperRegistry, ProviderConfig.rpcSerializerProtocol,serviceScanPackages);
        //return new HttpRPCServer(ip,port, zookeeperRegistry,ProviderConfig.rpcSerializerProtocol); 切换不同server
    }
/*    @Bean
   public ServiceAnnotationBeanPostProcessor annotationBeanPostProcessor(){
        return new ServiceAnnotationBeanPostProcessor(scanPackage);
    }*/
}
