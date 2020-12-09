/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client;

import com.z.rpc.client.AbstractRpcClient;
import com.z.rpc.client.AsyncRPCCallback;
import com.z.rpc.client.loadbalance.LoadBalanceStrategy;
import com.z.rpc.client.RpcClient;
import com.z.rpc.common.CallType;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.config.ConsumerConfig;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.zookeeper.ZookeeperRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
public class HelloWorldSimpleClientTest {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldSimpleClientTest.class);

    public static void main(String args[]) throws Exception {
        String registryAddress = "192.168.32.54:2181";
        Registry registry = new ZookeeperRegistry(registryAddress);
        LoadBalanceStrategy loadBalanceStrategy = LoadBalanceStrategy.RANDOM;
        RpcSerializerProtocol rpcSerializerProtocol = RpcSerializerProtocol.HESSIANSERIALIZE;
        // AbstractRpcClient rpcClient0= new HttpRpcClient(registry);
        //自定义配置注册中心，序列化协议
        AbstractRpcClient rpcClient = new RpcClient(registry, rpcSerializerProtocol);
        rpcClient.startup();
        //同步调用
        HelloService helloService1 = rpcClient.proxy(HelloService.class);
        Person person1 = new Person("STEPAN", "CURRY");
        
        String result1 = helloService1.hello(person1);
        System.out.println(result1);

        //熔断调用
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(10 * 1000L);
        consumerConfig.setCallType(CallType.SYNC);
        consumerConfig.setRetries(0);
        consumerConfig.setEnableHystrix(true);
        consumerConfig.setFallBackClassName(HelloServiceFallback.class.getName());

        HelloService helloService2 = rpcClient.proxy(HelloService.class,consumerConfig);
        Person person2 = new Person("LEBRON", "JAMES");
        String result2 = helloService2.hello("hhhhhhh");
        System.out.println(result2);

        //CALLBACK模式调用
        consumerConfig.setCallType(CallType.CALLBACK);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        rpcClient.registerCallback(HelloService.class.getName()+"#"+"hello",new AsyncRPCCallback() {
            @Override
            public void success(Object result) {
                countDownLatch.countDown();
                System.out.println("异步回调结果:" + (String) result);
            }

            @Override
            public void fail(Exception e) {
                logger.error("异步调用出错", e);
                countDownLatch.countDown();
            }
        });
        HelloService helloService3 = rpcClient.proxy(HelloService.class,consumerConfig, loadBalanceStrategy);
        Person person3 = new Person("KOBE", "BRANT");
        helloService3.hello(person3);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
