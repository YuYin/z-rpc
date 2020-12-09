package com.z.rpc.common.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 客户端解码器
 */
public class ResponseMessagePacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ResponsePacket packet = new ResponsePacket();
        // 基础包decode
        packet.decode(in);
        // error code
        int codeLength=in.readInt();
        packet.setCode(in.readCharSequence(codeLength,ProtocolConstant.UTF_8).toString());
        // message
        int messageLength = in.readInt();
        packet.setMessage(in.readCharSequence(messageLength, ProtocolConstant.UTF_8).toString());

        //success
        boolean  success=in.readBoolean();
        packet.setSuccess(success);
        
        // payload - ByteBuf实例
        int payloadLength = in.readInt();
        packet.setPayload(in.readBytes(payloadLength));
        out.add(packet);
    }
}
