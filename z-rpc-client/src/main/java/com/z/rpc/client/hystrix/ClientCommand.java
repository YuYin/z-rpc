package com.z.rpc.client.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.z.rpc.common.filter.FilterInvoker;
import com.z.rpc.common.protocol.RequestPacket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

public class ClientCommand extends HystrixCommand<Object> {

    private static Logger logger = LoggerFactory.getLogger(ClientCommand.class);

    private FilterInvoker<?> invoker;
    private RequestPacket requestPacket;
    private String fallbackClassName;

    private Thread mainThread;

    public ClientCommand(Setter setter, FilterInvoker<?> invoker, RequestPacket requestPacket, String  fallbackClassName,Thread mainThread) {
        super(setter);
        this.invoker = invoker;
        this.requestPacket = requestPacket;
        this.fallbackClassName = fallbackClassName;
        this.mainThread=mainThread;
    }

    protected Object run() throws Exception {
        Object result = null;
        try {
            result = invoker.invoke(requestPacket);
        } catch (Throwable throwable) {
             //如果远程调用异常，抛出异常执行降级逻辑
              throw new HystrixRuntimeException(HystrixRuntimeException.FailureType.COMMAND_EXCEPTION, ClientCommand.class,
                        throwable.getMessage(), throwable, null);
        }
        return result;
    }

    @Override
    protected Object getFallback() {
        if (StringUtils.isEmpty(fallbackClassName)) {
            //抛出原本的异常
            return super.getFallback();
        }
        try {
             ServiceLoader<Fallback> serviceLoader = ServiceLoader.load(Fallback.class, mainThread.getContextClassLoader());
                Iterator iterator = serviceLoader.iterator();
                Fallback fallback = null;
                while (iterator.hasNext()) {
                    fallback = (Fallback) iterator.next();
                    if(fallback.getClass().getName().equals(fallbackClassName)){
                         return fallback.invoke();
                    }
                }
        } catch (RuntimeException ex) {
            logger.error("fallback failed", ex);
            throw ex;
        }
        return null;
    }

}
