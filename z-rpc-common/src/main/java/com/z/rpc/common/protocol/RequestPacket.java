package com.z.rpc.common.protocol;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequestPacket extends BasePacket {

    /**
     * 接口全类名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数签名
     */
    private String[] methodArgumentSignatures;

    /**
     * 方法参数
     */
    private Object[] methodArguments;

}
