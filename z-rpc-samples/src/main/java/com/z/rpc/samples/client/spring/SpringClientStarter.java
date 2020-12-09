/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
public class SpringClientStarter {

    public static void main(String args[]) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.scan("com.zds.rpc.samples.client");
        annotationConfigApplicationContext.register(RPCClientConfig.class);
        annotationConfigApplicationContext.getBeanFactory().addBeanPostProcessor(new RPCClientBPP(annotationConfigApplicationContext));
        annotationConfigApplicationContext.refresh();
        try {
            System.out.println(">>>>>>>>>>>>>等待服务初始化完成<<<<<<<<<<<<<<<<<<");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(">>>>>>>>>>>>>服务初始化完成<<<<<<<<<<<<<<<<<<");
        System.out.println(">>>>>>>>>>>>开始测试调用服务<<<<<<<<<<<<<<<<<<");
        RPCClientComponent rpcClientComponent = (RPCClientComponent) annotationConfigApplicationContext.getBean("rpcClientComponent");
        int count = 1000;
        long t1 = System.currentTimeMillis();
        System.out.println(">>>>>>>>>>>>>远程调用测试开始<<<<<<<<<<<<<<<<<<");
        for (int i = 0; i < count; i++) {
            System.out.println("i=" + i + ",返回结果" + rpcClientComponent.doHelloWorldTest("hahahaha"));
        }
        System.out.println("远程调用方法#doHelloWorldTest#" + count + "次用时" + (System.currentTimeMillis() - t1) + "ms");
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            System.out.println("i=" + i + ",返回结果list大小:size=" + rpcClientComponent.doPersonTest("yinyu", 2).size());
        }
        System.out.println("远程调用方法#doPersonTest#" + count + "次用时" + (System.currentTimeMillis() - t2) + "ms");

        System.out.println(">>>>>>>>>>>>>远程调用测试结束<<<<<<<<<<<<<<<<<<");
        while ((!Thread.currentThread().isInterrupted())) {
            try {
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
