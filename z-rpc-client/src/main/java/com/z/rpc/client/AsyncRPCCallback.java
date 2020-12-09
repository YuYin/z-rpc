package com.z.rpc.client;

public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
