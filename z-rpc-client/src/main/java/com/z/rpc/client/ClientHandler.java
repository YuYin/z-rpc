package com.z.rpc.client;

import com.z.rpc.common.protocol.ResponsePacket;
import com.z.rpc.common.serialize.RPCSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends SimpleChannelInboundHandler<ResponsePacket> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private RPCSerializer rpcSerializer;
    private ConcurrentHashMap<String, InvokeFuture> pendingRPC = new ConcurrentHashMap<>();

    public  ClientHandler(RPCSerializer rpcSerializer){
        this.rpcSerializer=rpcSerializer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ResponsePacket response) throws Exception {
        Object targetPayload = response.getPayload();
        if (targetPayload instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) targetPayload;
            int readableByteLength = byteBuf.readableBytes();
            byte[] bytes = new byte[readableByteLength];
            byteBuf.readBytes(bytes);
            targetPayload = rpcSerializer.deserialize(bytes, null);
            byteBuf.release();
        }
        response.setPayload(targetPayload);
        String requestId = response.getRequestId();
        InvokeFuture invokeFuture = PendingFutureManager.pendingRPC.get(requestId);
        if (invokeFuture != null) {
            pendingRPC.remove(requestId);
            invokeFuture.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }

}
