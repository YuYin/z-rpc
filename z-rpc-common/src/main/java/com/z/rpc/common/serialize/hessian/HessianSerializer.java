/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common.serialize.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.z.rpc.common.exception.RpcException;
import com.z.rpc.common.serialize.RPCSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/22
 */
public class HessianSerializer implements RPCSerializer {
    //unused
    private HessianSerializePool pool = HessianSerializePool.getHessianPoolInstance();

    @Override
    public byte[] serialize(final Object object) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(byteArrayOutputStream);
        try {
            ho.startMessage();
            ho.writeObject(object);
            ho.completeMessage();
            ho.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw  new RpcException(e);
        }
    }

    @Override
    public Object deserialize(byte[] data, Class aClass) {
        Object result = null;
         ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            Hessian2Input hi = new Hessian2Input(byteArrayInputStream);
            hi.startMessage();
            result = hi.readObject();
            hi.completeMessage();
            hi.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
             throw  new RpcException(e);
        }
        return result;
    }
}
