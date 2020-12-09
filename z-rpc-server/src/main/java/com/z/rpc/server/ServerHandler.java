package com.z.rpc.server;

import com.google.common.base.Throwables;
import com.z.rpc.common.filter.FilterBuilder;
import com.z.rpc.common.protocol.MessageType;
import com.z.rpc.common.protocol.RequestPacket;
import com.z.rpc.common.protocol.ResponsePacket;
import com.z.rpc.common.serialize.RPCSerializer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * RPC Handler RPC request processor
 */
public class ServerHandler extends BaseServerHandler<RequestPacket> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    private static final String SUCCESS="success";
    private static final String FAILED="failed";



    public ServerHandler(Map<String, Object> handlerMap, FilterBuilder filterBuilder, ThreadPoolExecutor threadPoolExecutor, RPCSerializer rpcSerializer) {
        this.handlerMap = handlerMap;
        this.filterBuilder = filterBuilder;
        this.threadPoolExecutor = threadPoolExecutor;
        this.rpcSerializer = rpcSerializer;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final RequestPacket request) throws Exception {
        threadPoolExecutor.submit(() -> {
            logger.debug("Receive request " + request.getRequestId());
            ResponsePacket response = null;
            try {
                Object result = handle(request);
                response = buildResponse(request, result, "", SUCCESS,true);
            } catch (Throwable t) {
               Throwable throwable= Throwables.getRootCause(t);
                response = buildResponse(request, null, throwable.getMessage(), FAILED,false);
                logger.error("RPC Server handle request error", t);
            }
            ctx.writeAndFlush(response)
                    .addListener((ChannelFutureListener) channelFuture -> logger.debug("Send response for request " + request.getRequestId()))
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.channel().close();      // beat 3N, close if idle
            logger.debug(" z-rpc provider netty server close an idle channel.");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private ResponsePacket buildResponse(RequestPacket request, Object result, String message, String code,boolean success) {
        ResponsePacket response = new ResponsePacket();
        response.setRequestId(request.getRequestId());
        response.setPayload(result);
        response.setMagicNumber(request.getMagicNumber());
        response.setVersion(request.getVersion());
        response.setRequestId(request.getRequestId());
        response.setAttachments(request.getAttachments());
        response.setMessageType(MessageType.RESPONSE);
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(success);
        return response;
    }
}
