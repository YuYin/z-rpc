package com.z.rpc.common.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResponsePacket extends BasePacket {
    /**
     * 状态码
     */
    private String code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 数据
     */
    private Object payload;

    /**
     * 处理结果
     */
    private boolean success;
}
