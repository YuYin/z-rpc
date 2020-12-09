package com.z.rpc.common.protocol;

import com.z.rpc.common.serialize.RPCSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

/**
 * 客户端编码器
 */
@RequiredArgsConstructor
public class RequestMessagePacketEncoder extends MessageToByteEncoder<RequestPacket> {

    private final RPCSerializer serializer;

    @Override
    protected void encode(ChannelHandlerContext context, RequestPacket packet, ByteBuf out) throws Exception {
        // 基础包encode
        packet.encode(out);
        // 接口全类名
        out.writeInt(packet.getInterfaceName().length());
        out.writeCharSequence(packet.getInterfaceName(), ProtocolConstant.UTF_8);
        // 方法名
        out.writeInt(packet.getMethodName().length());
        out.writeCharSequence(packet.getMethodName(), ProtocolConstant.UTF_8);
        if (null != packet.getMethodArgumentSignatures()) {
            int len = packet.getMethodArgumentSignatures().length;
            // 方法参数签名数组长度
            out.writeInt(len);
            for (int i = 0; i < len; i++) {
                String methodArgumentSignature = packet.getMethodArgumentSignatures()[i];
                out.writeInt(methodArgumentSignature.length());
                out.writeCharSequence(methodArgumentSignature, ProtocolConstant.UTF_8);
            }
        } else {
            out.writeInt(0);
        }
        if (null != packet.getMethodArguments()) {
            int len = packet.getMethodArguments().length;
            // 方法参数数组长度
            out.writeInt(len);
            for (int i = 0; i < len; i++) {
                byte[] bytes = serializer.serialize(packet.getMethodArguments()[i]);
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            }
        } else {
            out.writeInt(0);
        }
    }
}
