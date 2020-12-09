/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.client.connection;

import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.registry.ServerNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/5
 *         客户端连接管理器
 */
public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);


    public static Map<String, Connection> initConnection(List<ServerNode> list,
                                                           NioEventLoopGroup nioEventLoopGroup,
                                                           ChannelInitializer channelInitializer,
                                                           RpcSerializerProtocol rpcSerializerProtocol) {
        Map<String, Connection> connectionMap = new HashMap<>();
        for (ServerNode serverNode : list) {
            connectionMap.put(serverNode.getAddress(), buildSingleLongConnection(serverNode, nioEventLoopGroup, channelInitializer, rpcSerializerProtocol));
        }
        return connectionMap;
    }

    public static Connection addConnection(ServerNode serverNode,
                                           NioEventLoopGroup nioEventLoopGroup,
                                           ChannelInitializer channelInitializer,
                                           RpcSerializerProtocol rpcSerializerProtocol) {
        return buildSingleLongConnection(serverNode, nioEventLoopGroup, channelInitializer, rpcSerializerProtocol);
    }

    private static Connection buildSingleLongConnection(ServerNode serverNode,
                                                        NioEventLoopGroup nioEventLoopGroup,
                                                        ChannelInitializer channelInitializer,
                                                        RpcSerializerProtocol rpcSerializerProtocol) {
        String[] array = serverNode.getAddress().split(":");
        InetSocketAddress remotePeer = null;
        if (array.length == 2) { // Should check IP and port
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            remotePeer = new InetSocketAddress(host, port);
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(channelInitializer);

        bootstrap.option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        //同步等待连接建立成功
        logger.info("开始建立连接:{}", serverNode.getAddress());
        ChannelFuture channelFuture = bootstrap.connect(remotePeer).syncUninterruptibly();
        logger.info("建立连接成功:{}", serverNode.getAddress());
        Connection connection = new Connection();
        connection.setAddress(serverNode.getAddress());
        connection.setChannel(channelFuture.channel());
        connection.setRpcSerializerProtocol(rpcSerializerProtocol);
        return connection;
    }
}
