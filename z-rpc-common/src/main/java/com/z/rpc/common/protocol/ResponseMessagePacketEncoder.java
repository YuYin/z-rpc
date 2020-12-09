package com.z.rpc.common.protocol;

import com.z.rpc.common.serialize.RPCSerializer;
import com.z.rpc.common.utils.ByteBufferUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;

/**
 * 服务端编码器
 */
@RequiredArgsConstructor
public class ResponseMessagePacketEncoder extends MessageToByteEncoder<ResponsePacket> {

    private final RPCSerializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponsePacket packet, ByteBuf out) throws Exception {
        // 基础包encode
        packet.encode(out);
        // error code
        String code=packet.getCode();
        ByteBufferUtils.INSTANCE.encodeUtf8CharSequence(out, code);
        // message
        String message = packet.getMessage();
        ByteBufferUtils.INSTANCE.encodeUtf8CharSequence(out, message);
        //success
         out.writeBoolean(packet.isSuccess());
        // payload
        byte[] bytes = serializer.serialize(packet.getPayload());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
