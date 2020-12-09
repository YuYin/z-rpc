package com.z.rpc.client.proxy;

import com.google.common.collect.Lists;
import com.z.rpc.client.*;
import com.z.rpc.client.connection.Connection;
import com.z.rpc.client.loadbalance.LoadBalanceStrategy;
import com.z.rpc.common.CallType;
import com.z.rpc.common.RPCSystemConfig;
import com.z.rpc.common.exception.RpcException;
import com.z.rpc.common.filter.FilterBuilder;
import com.z.rpc.common.filter.FilterInvoker;
import com.z.rpc.common.protocol.MessageType;
import com.z.rpc.common.protocol.ProtocolConstant;
import com.z.rpc.common.protocol.RequestPacket;
import com.z.rpc.common.thread.NamedThreadFactory;
import com.z.rpc.common.utils.UUIDUtils;
import com.z.rpc.config.ConsumerConfig;
import com.z.rpc.registry.ServerNode;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientProxy<T> implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);
    private ConsumerConfig consumerConfig;
    private LoadBalanceStrategy loadBalanceStrategy = LoadBalanceStrategy.RANDOM;
    private AbstractRpcClient connectClient;

    private BaseRemoting rpcRemoting = new RpcRemoting();
    private FilterBuilder filterBuilder;
    private AsyncRPCCallback asyncRPCCallback;

    private static final long RETRY_FAILED_PERIOD = 5;

    private volatile Timer failTimer = null;


    public ClientProxy(ConsumerConfig consumerConfig, LoadBalanceStrategy loadBalanceStrategy, AbstractRpcClient connectClient) {
        this.consumerConfig = consumerConfig;
        this.loadBalanceStrategy = loadBalanceStrategy;
        this.connectClient = connectClient;
    }

    public ClientProxy(AbstractRpcClient connectClient, FilterBuilder filterBuilder) {
        this.connectClient = connectClient;
        this.filterBuilder = filterBuilder;
    }
        public ClientProxy(AbstractRpcClient connectClient,ConsumerConfig consumerConfig, FilterBuilder filterBuilder) {
        this.connectClient = connectClient;
        this.filterBuilder = filterBuilder;
        this.consumerConfig=consumerConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RequestPacket request = new RequestPacket();
        request.setMagicNumber(ProtocolConstant.MAGIC_NUMBER);
        request.setVersion(ProtocolConstant.VERSION);
        request.setRequestId(UUIDUtils.getUUID());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setMessageType(MessageType.REQUEST);
        List<String> methodArgumentSignatures = Lists.newArrayList();
        for (Class<?> klass : method.getParameterTypes()) {
            methodArgumentSignatures.add(klass.getName());
        }
        request.setMethodArgumentSignatures(methodArgumentSignatures.toArray(new String[methodArgumentSignatures.size()]));
        request.setMethodArguments(args);
        setMethodCallBack(connectClient.getMethodCallback(method.getDeclaringClass().getName()+"#"+method.getName()));
        if(consumerConfig!=null){
            //熔断降级相关参数
          request.getAttachments().put(RPCSystemConfig.ENABLE_HYSTRIX_KEY,String.valueOf(consumerConfig.isEnableHystrix()));
          if(consumerConfig.isEnableHystrix()&&StringUtils.isNotEmpty(consumerConfig.getFallBackClassName())){
            request.getAttachments().put(RPCSystemConfig.FALL_BACK_CLASS_KEY,consumerConfig.getFallBackClassName());
          }
        }
        if (filterBuilder != null) {
            FilterInvoker filterInvoker = filterBuilder.buildChain(new FilterInvoker() {
                @Override
                public Class getInterface() {
                    return null;
                }

                @Override
                public Object invoke(RequestPacket request) throws Throwable {
                    return invoke0(request);
                }

                @Override
                public void destroy() {

                }
            });
            return filterInvoker.invoke(request);
        } else {
            return invoke0(request);
        }
    }

    private Object invoke0(RequestPacket request) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return doInvoke(request);
        } catch (Throwable e) {
            //future调用模式增加重试机制
            if(consumerConfig!=null&&consumerConfig.getRetries()>0&&consumerConfig.getCallType().equals(CallType.FUTURE)){
               LOGGER.error("Failback to invoke method " + request.getMethodName() + ", wait for retry in background. Ignored exception: "
                    + e.getMessage() + ", ", e);
                addFailed(request);
            }
           throw  e;
        }
    }

    private void addFailed(RequestPacket requestPacket) {
        if (failTimer == null) {
            synchronized (this) {
                if (failTimer == null) {
                    failTimer = new HashedWheelTimer(
                            new NamedThreadFactory("failback-timer", true),
                            1,
                            TimeUnit.SECONDS, 32);
                }
            }
        }
        failTimer.newTimeout(new RetryTimerTask(requestPacket, consumerConfig.getRetries(), RETRY_FAILED_PERIOD), RETRY_FAILED_PERIOD, TimeUnit.SECONDS);
    }

    private Object doInvoke(RequestPacket request) throws InterruptedException, ExecutionException, TimeoutException {
        List<ServerNode> serverNodeList = connectClient.getServerNodeList();
        String serverAddress = select(serverNodeList);
        LOGGER.info("获取到服务端地址:{}", serverAddress);
        Connection connection = connectClient.getConnection(serverAddress);
        //默认同步模式
        if (consumerConfig == null || consumerConfig.getCallType() == CallType.SYNC) {
            return rpcRemoting.invokeSync(request, connection, RPCSystemConfig.SYSTEM_PROPERTY_SYNC_CALL_TIMEOUT);
        }
        //ONEWAY模式
        if (consumerConfig != null && consumerConfig.getCallType() == CallType.ONEWAY) {
            rpcRemoting.oneway(request, connection);
            return null;
        }
        //FUTURE调用模式
        if (consumerConfig != null && consumerConfig.getCallType() == CallType.FUTURE) {
            long timeout = RPCSystemConfig.SYSTEM_PROPERTY_SYNC_CALL_TIMEOUT;
            if (consumerConfig != null && consumerConfig.getTimeout() > 0) {
                timeout = consumerConfig.getTimeout();
            }
            InvokeFuture invokeFuture = rpcRemoting.invokeWithFuture(request, connection);
            return invokeFuture.get(timeout, TimeUnit.MILLISECONDS);
        }
        //CALLBACK调用模式
        if (consumerConfig != null && consumerConfig.getCallType() == CallType.CALLBACK) {
            if(asyncRPCCallback==null){
                throw new RpcException("请先注册对应方法的回调函数");
            }
            rpcRemoting.invokeWithCallback(request, connection, asyncRPCCallback);
        }

        return null;
    }

    private String select(List<ServerNode> serverNodeList) {
        boolean directMode = consumerConfig != null && StringUtils.isNotEmpty(consumerConfig.getDirectUrl());
        if (directMode) {
            if (!StringUtils.isNotEmpty(consumerConfig.getDirectUrl())) {
                throw new RpcException("直连模式,直连地址为空");
            }
            return consumerConfig.getDirectUrl();
        }
        return loadBalanceStrategy.loadBalance.route(serverNodeList);
    }

    private void setMethodCallBack(AsyncRPCCallback callBack){
        this.asyncRPCCallback=callBack;
    }

    class RetryTimerTask implements TimerTask {
        private  final Logger logger = LoggerFactory.getLogger(ClientProxy.class);

        private RequestPacket requestPacket;
        private final int retries;
        private final long tick;
        private int retryTimes = 0;

        RetryTimerTask(RequestPacket requestPacket, int retries, long tick) {
            this.requestPacket = requestPacket;
            this.retries = retries;
            this.tick = tick;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
             try {
                 doInvoke(requestPacket);
             }catch (Throwable e){
                     logger.error("Failed retry to invoke method " + requestPacket.getMethodName() + ", waiting again.", e);
                if ((++retryTimes) >= retries) {
                    logger.error("Failed retry times exceed threshold (" + retries + "), We have to abandon, invocation method->" + requestPacket.getMethodName());
                } else {
                    rePut(timeout);
                }
             }
        }
          private void rePut(Timeout timeout) {
            if (timeout == null) {
                return;
            }
            Timer timer = timeout.timer();
            if (timeout.isCancelled()) {
                return;
            }
            timer.newTimeout(timeout.task(), tick, TimeUnit.SECONDS);
        }
    }

}
