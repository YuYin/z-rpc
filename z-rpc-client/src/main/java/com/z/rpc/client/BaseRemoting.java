/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client;

import com.z.rpc.client.connection.Connection;
import com.z.rpc.common.protocol.RequestPacket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/6
 */
public abstract class BaseRemoting {

     public abstract Object invokeSync(RequestPacket rpcRequest, Connection connection, final long timeoutMillis) throws TimeoutException, ExecutionException, InterruptedException;
    public abstract InvokeFuture invokeWithFuture(RequestPacket rpcRequest, Connection connection) throws InterruptedException;
    public abstract void oneway(RequestPacket rpcRequest, Connection connection) throws InterruptedException;
    public abstract void invokeWithCallback(RequestPacket rpcRequest, Connection connection, AsyncRPCCallback asyncRPCCallback) throws InterruptedException;
}
