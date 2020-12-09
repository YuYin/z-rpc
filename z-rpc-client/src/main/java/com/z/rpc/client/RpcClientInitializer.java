package com.z.rpc.client;

import com.z.rpc.common.protocol.RequestMessagePacketEncoder;
import com.z.rpc.common.protocol.ResponseMessagePacketDecoder;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    private RpcSerializerProtocol rpcSerializerProtocol;
    public RpcClientInitializer(RpcSerializerProtocol rpcSerializerProtocol){
       this.rpcSerializerProtocol = rpcSerializerProtocol;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        //inBound
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));//粘包拆包解决
        cp.addLast(new LengthFieldPrepender(4));
        cp.addLast(new RequestMessagePacketEncoder(rpcSerializerProtocol.rpcSerializer));
                cp.addLast(new ResponseMessagePacketDecoder());
        cp.addLast(new ClientHandler(rpcSerializerProtocol.rpcSerializer)); //可添加多个handler,pipeline handler有热插拔功能,动态删除某个handler:ctx.pipeline().remove(this);
    }
}
