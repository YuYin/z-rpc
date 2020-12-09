package com.z.rpc.common.exception;

public class RpcException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    public RpcException(String msg) {
        super(msg);
    }

    public RpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

}