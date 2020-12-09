package com.z.rpc.common.protocol;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BasePacket implements Serializable {

    /**
     * 魔数
     */
    private int magicNumber;

    /**
     * 版本号
     */
    private int version;

    /**
     * 请求唯一ID
     */
    private String requestId;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 附件 - K-V形式
     */
    private Map<String, String> attachments = new HashMap<>();

    /**
     * 添加附件
     */
    public void addAttachment(String key, String value) {
        attachments.put(key, value);
    }

    /**
     * 基础包encode
     *
     * @param out out
     */
    public void encode(ByteBuf out) {
        // 魔数
        out.writeInt(getMagicNumber());
        // 版本
        out.writeInt(getVersion());
        // 流水号
        out.writeInt(getRequestId().length());
        out.writeCharSequence(getRequestId(), ProtocolConstant.UTF_8);
        // 消息类型
        out.writeByte(getMessageType().getType());
        // 附件size
        Map<String, String> attachments = getAttachments();
        out.writeInt(attachments.size());
        // 附件内容
        attachments.forEach((k, v) -> {
            out.writeInt(k.length());
            out.writeCharSequence(k, ProtocolConstant.UTF_8);
            out.writeInt(v.length());
            out.writeCharSequence(v, ProtocolConstant.UTF_8);
        });
    }

    /**
     * 基础包decode
     *
     * @param in in
     */
    public void decode(ByteBuf in) {
        // 魔数
        setMagicNumber(in.readInt());
        // 版本
        setVersion(in.readInt());
        // 流水号
        int serialNumberLength = in.readInt();
        setRequestId(in.readCharSequence(serialNumberLength, ProtocolConstant.UTF_8).toString());
        // 消息类型
        byte messageTypeByte = in.readByte();
        setMessageType(MessageType.fromValue(messageTypeByte));
        // 附件
        Map<String, String> attachments = Maps.newHashMap();
        setAttachments(attachments);
        int attachmentSize = in.readInt();
        if (attachmentSize > 0) {
            for (int i = 0; i < attachmentSize; i++) {
                int keyLength = in.readInt();
                String key = in.readCharSequence(keyLength, ProtocolConstant.UTF_8).toString();
                int valueLength = in.readInt();
                String value = in.readCharSequence(valueLength, ProtocolConstant.UTF_8).toString();
                attachments.put(key, value);
            }
        }
    }
}
