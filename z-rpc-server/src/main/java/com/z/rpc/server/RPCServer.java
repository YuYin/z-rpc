/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.server;

import com.z.rpc.common.filter.FilterBuilder;
import com.z.rpc.common.Constants;
import com.z.rpc.common.filter.FilterBuilderBase;
import com.z.rpc.common.protocol.RequestMessagePacketDecoder;
import com.z.rpc.common.protocol.ResponseMessagePacketEncoder;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.ServerNode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
public class RPCServer extends AbstractRemoteServer {

    private static final Logger logger = LoggerFactory.getLogger(RPCServer.class);


    private RpcSerializerProtocol rpcSerializerProtocol;

    private FilterBuilder filterBuilder = new FilterBuilderBase(Constants.PROVIDER);

    protected Registry registry;

    protected Map<String,String> serverAddressToRegistryPathMap=new HashMap<>();

    public RPCServer(String ip, int port, Registry registry, RpcSerializerProtocol rpcSerializerProtocol, String serviceScanPackages) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException(String.format(
                    "Illegal port value: %d, which should between 0 and 65535.", port));
        }
        this.ip = ip;
        this.port = port;
        this.registry = registry;
        this.rpcSerializerProtocol = rpcSerializerProtocol;
        this.serviceScanPackages=serviceScanPackages;
    }


    @Override
    protected void beforeStart() throws Exception {
        super.initThreadPool();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                  @Override
                  public void run() {
                      try {
                          logger.info("execute shutdown... ");
                          shutdown();
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
              }));
    }

    @Override
    public boolean doStart() throws Exception {
        if (bossGroup == null && workerGroup == null) {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new RequestMessagePacketDecoder())
                                    .addLast(new ResponseMessagePacketEncoder(rpcSerializerProtocol.rpcSerializer))
                                    .addLast(new ServerHandler(serviceMap, filterBuilder, threadPoolExecutor,rpcSerializerProtocol.rpcSerializer));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            ChannelFuture future = bootstrap.bind(ip, port).sync();
            return future.isSuccess();
        }
        return false;
    }

    @Override
    public void afterStarted() {
        //注册服务地址到zk
        if (registry != null) {
            ServerNode server = new ServerNode(ip + ":" + port);
           String path= registry.register(server);
            logger.info("注册服务地址成功:{}",server.getAddress());
            serverAddressToRegistryPathMap.put(server.getAddress(),path);
        }
        //反射创建服务实例
        createServiceInstance();
    }
    public void createServiceInstance() {
        org.reflections.Reflections reflections = new org.reflections.Reflections(serviceScanPackages);
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcService.class);
        for (Class type : classes) {
            RpcService rpcService= (RpcService) type.getAnnotation(RpcService.class);
            String interfaceName=rpcService.interfaceClass().getName();
            if(!serviceMap.containsKey(interfaceName)){
                try {
                    logger.info("反射创建服务实例:{}",interfaceName);
                    serviceMap.put(interfaceName, type.newInstance());
                } catch (InstantiationException e) {
                   logger.error("实例化服务失败",e);
                } catch (IllegalAccessException e) {
                    logger.error("实例化服务失败",e);
                }
            }
        }
    }

    @Override
    public boolean doStop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        try {
            registry.unregister(ip+":"+port);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}
