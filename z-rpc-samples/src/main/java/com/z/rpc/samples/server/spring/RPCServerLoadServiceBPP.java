/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.server.spring;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 *  server loadservices which proxied by spring cglib
 */
/*public class RPCServerLoadServiceBPP implements BeanPostProcessor {
    private ApplicationContext applicationContext;

    public RPCServerLoadServiceBPP(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //rpcServer在初始化前加载service
        if(beanName.equals(Constants.SERVER_BEAN_NAME)){
            Map<String,Object> objectMap= applicationContext.getBeansWithAnnotation(RpcService.class);
            for(Map.Entry<String,Object> entry:objectMap.entrySet()){
               String interfaceName = entry.getValue().getClass().getAnnotation(RpcService.class).interfaceClass().getName();
                RemoteServer remoteServer = (RemoteServer) bean;
                remoteServer.loadService(interfaceName,entry.getValue());
            }
        }
         return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}*/
