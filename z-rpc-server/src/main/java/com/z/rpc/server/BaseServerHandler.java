/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.server;

import com.z.rpc.common.filter.FilterBuilder;
import com.z.rpc.common.filter.FilterInvoker;
import com.z.rpc.common.protocol.RequestPacket;
import com.z.rpc.common.serialize.RPCSerializer;
import com.z.rpc.common.utils.ClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/28
 */
public abstract class BaseServerHandler<T> extends SimpleChannelInboundHandler<T> {

    protected ThreadPoolExecutor threadPoolExecutor;
    protected Map<String, Object> handlerMap;
    protected FilterBuilder filterBuilder;
    protected RPCSerializer rpcSerializer;



    protected Object handle(RequestPacket request) throws Throwable {
        String className = request.getInterfaceName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        List<Class<?>> parameterTypeList=new ArrayList();
        for (String methodArgumentClassName:request.getMethodArgumentSignatures()  ) {
         parameterTypeList.add(ClassUtils.forName(methodArgumentClassName,Thread.currentThread().getContextClassLoader()));
        }
        Class<?>[] parameterTypeArray = parameterTypeList.toArray(new Class[parameterTypeList.size()]);

        // JDK reflect
        Method method = serviceClass.getMethod(methodName, parameterTypeArray);
        method.setAccessible(true);
        //将方法参数值byteBuf数组转换为具体类型的参数值数组
        Object methodArguments[]=convertByteBuf(parameterTypeArray,request.getMethodArguments());
        request.setMethodArguments(methodArguments);

        // Cglib reflect
//        FastClass serviceFastClass = FastClass.create(serviceClass);
//        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return invoke(method, request, serviceClass);
    }

    protected Object invoke(Method mi, RequestPacket request, Class aClass) throws Throwable {
        //   FilterBuilder filterBuilder = filterBuilderBase;
        if (filterBuilder != null) {
            FilterInvoker filterInvoker = filterBuilder.buildChain(new FilterInvoker() {

                @Override
                public Class getInterface() {
                    return aClass.getInterfaces()[0];
                }

                @Override
                public Object invoke(RequestPacket request) throws Throwable {
                    return mi.invoke(handlerMap.get(request.getInterfaceName()), request.getMethodArguments());
                }

                @Override
                public void destroy() {

                }
            });
            return filterInvoker.invoke(request);
        } else {
            return mi.invoke(handlerMap.get(request.getInterfaceName()), request.getMethodArguments());
        }
    }
    private Object[] convertByteBuf(Class<?> parameterTypes[],Object byteBufArgumentArray[]){
            int len = parameterTypes.length;
            Object[] arguments = new Object[len];
            for (int i = 0; i < len; i++) {
                ByteBuf byteBuf = (ByteBuf) byteBufArgumentArray[i];
                int readableBytes = byteBuf.readableBytes();
                byte[] bytes = new byte[readableBytes];
                byteBuf.readBytes(bytes);
                arguments[i] = rpcSerializer.deserialize(bytes, parameterTypes[i]);
                byteBuf.release();
            }
       return arguments;
    }
}
