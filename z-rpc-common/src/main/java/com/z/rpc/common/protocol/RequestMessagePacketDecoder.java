package com.z.rpc.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 服务端解码器
 */
@RequiredArgsConstructor
public class RequestMessagePacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> list) throws Exception {
        RequestPacket packet = new RequestPacket();
        // 基础包decode
        packet.decode(in);
        // 接口全类名
        int interfaceNameLength = in.readInt();
        packet.setInterfaceName(in.readCharSequence(interfaceNameLength, ProtocolConstant.UTF_8).toString());
        // 方法名
        int methodNameLength = in.readInt();
        packet.setMethodName(in.readCharSequence(methodNameLength, ProtocolConstant.UTF_8).toString());
        // 方法参数签名
        int methodArgumentSignatureArrayLength = in.readInt();
        if (methodArgumentSignatureArrayLength > 0) {
            String[] methodArgumentSignatures = new String[methodArgumentSignatureArrayLength];
            for (int i = 0; i < methodArgumentSignatureArrayLength; i++) {
                int methodArgumentSignatureLength = in.readInt();
                methodArgumentSignatures[i] = in.readCharSequence(methodArgumentSignatureLength, ProtocolConstant.UTF_8).toString();
            }
            packet.setMethodArgumentSignatures(methodArgumentSignatures);
        }
        // 方法参数
        int methodArgumentArrayLength = in.readInt();
        if (methodArgumentArrayLength > 0) {
            // 这里的Object[]实际上是ByteBuf[]
            Object[] methodArguments = new Object[methodArgumentArrayLength];
            for (int i = 0; i < methodArgumentArrayLength; i++) {
                int byteLength = in.readInt();
                methodArguments[i] = in.readBytes(byteLength);
            }
            packet.setMethodArguments(methodArguments);
        }
        list.add(packet);
    }
}
