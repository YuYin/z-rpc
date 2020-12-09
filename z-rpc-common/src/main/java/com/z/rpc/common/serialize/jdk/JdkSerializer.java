/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common.serialize.jdk;

import com.z.rpc.common.exception.RpcException;
import com.z.rpc.common.serialize.RPCSerializer;

import java.io.*;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/22
 */
public class JdkSerializer implements RPCSerializer {
    @Override
    public byte[] serialize(Object object) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        ){
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
             throw  new RpcException(e);
        }
    }

    @Override
    public Object deserialize(byte[] data, Class aClass) {
        try {
            try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
          ){
              Object o = objectInputStream.readObject();
              return (Serializable) o;
          }
        } catch (Exception e) {
            throw  new RpcException(e);
        } 
    }
}
