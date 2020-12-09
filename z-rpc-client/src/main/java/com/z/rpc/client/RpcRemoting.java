/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client;

import com.z.rpc.client.connection.Connection;
import com.z.rpc.common.RPCSystemConfig;
import com.z.rpc.common.protocol.RequestPacket;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/6
 */
public class RpcRemoting extends BaseRemoting {
    private static final Logger logger = LoggerFactory.getLogger(RpcRemoting.class);


    @Override
    public Object invokeSync(RequestPacket rpcRequest, Connection connection, long timeoutMillis) throws InterruptedException, TimeoutException, ExecutionException {

        InvokeFuture invokeFuture = new InvokeFuture(rpcRequest);
        PendingFutureManager.pendingRPC.put(rpcRequest.getRequestId(), invokeFuture);
        doInvoke(rpcRequest, connection);
        Object object = invokeFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        return object;
    }

    @Override
    public InvokeFuture invokeWithFuture(RequestPacket rpcRequest, Connection connection) throws InterruptedException {
        InvokeFuture invokeFuture = new InvokeFuture(rpcRequest);
        PendingFutureManager.pendingRPC.put(rpcRequest.getRequestId(), invokeFuture);
        doInvoke(rpcRequest, connection);
        return invokeFuture;
    }

    @Override
    public void oneway(RequestPacket rpcRequest, Connection connection) throws InterruptedException {
        doInvoke(rpcRequest, connection);
    }

    @Override
    public void invokeWithCallback(RequestPacket rpcRequest, Connection connection, AsyncRPCCallback asyncRPCCallback) throws InterruptedException {
        InvokeFuture invokeFuture = new InvokeFuture(rpcRequest);
        invokeFuture.addCallback(asyncRPCCallback);
        PendingFutureManager.pendingRPC.put(rpcRequest.getRequestId(), invokeFuture);
        doInvoke(rpcRequest, connection);
    }

    private void doInvoke(RequestPacket rpcRequest, Connection connection) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
      connection.getChannel().writeAndFlush(rpcRequest)
                    .addListener((ChannelFutureListener) future -> latch.countDown())
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        latch.await(RPCSystemConfig.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
