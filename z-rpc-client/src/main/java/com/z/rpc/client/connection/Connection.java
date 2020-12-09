/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.connection;


import com.z.rpc.common.serialize.RpcSerializerProtocol;
import io.netty.channel.Channel;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/6
 */
public class Connection {

    private String address;

    private Channel channel;

    private RpcSerializerProtocol rpcSerializerProtocol;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RpcSerializerProtocol getRpcSerializerProtocol() {
        return rpcSerializerProtocol;
    }

    public void setRpcSerializerProtocol(RpcSerializerProtocol rpcSerializerProtocol) {
        this.rpcSerializerProtocol = rpcSerializerProtocol;
    }
}
