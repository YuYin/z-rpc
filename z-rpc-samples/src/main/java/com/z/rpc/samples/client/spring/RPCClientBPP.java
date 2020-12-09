/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client.spring;

import com.z.rpc.client.AbstractRpcClient;
import com.z.rpc.client.RpcReference;
import com.z.rpc.common.exception.RpcException;
import com.z.rpc.config.ConsumerConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 * 根据@RpcReference注入service 代理
 */
public class RPCClientBPP implements BeanPostProcessor {
    private ApplicationContext applicationContext;
    public RPCClientBPP(ApplicationContext applicationContext){
        this.applicationContext=applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean==null){
            return bean;
        }
        if(!beanName.equals(Constants.CLIENT_BEAN_NAME)){
              Class aClass=bean.getClass();
            Field fields[]=aClass.getDeclaredFields();
            for(Field field:fields){
                RpcReference rpcReference=field.getAnnotation(RpcReference.class);
                if(rpcReference!=null){
                    AbstractRpcClient rpcClient= (AbstractRpcClient) applicationContext.getBean("rpcClient");
                    try {
                        field.setAccessible(true);
                        ConsumerConfig consumerConfig=new ConsumerConfig();
                        consumerConfig.setTimeout(rpcReference.timeout());
                        consumerConfig.setDirectUrl(rpcReference.directUrl());
                        consumerConfig.setCallType(rpcReference.callType());
                        consumerConfig.setRetries(rpcReference.retries());
                        consumerConfig.setEnableHystrix(rpcReference.enableHystrix());
                        consumerConfig.setFallBackClassName(rpcReference.fallBackClassName());
                        field.set(bean, rpcClient.proxy(field.getType(),consumerConfig,rpcReference.loadBalance()));
                    } catch (IllegalAccessException e) {
                        throw new RpcException(e);
                    }
                }
            }
        }
        return bean;
    }

}
