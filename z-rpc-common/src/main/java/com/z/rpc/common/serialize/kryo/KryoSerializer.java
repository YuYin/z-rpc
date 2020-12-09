/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common.serialize.kryo;

import com.z.rpc.common.serialize.RPCSerializer;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/22
 */
public class KryoSerializer implements RPCSerializer {
    @Override
    public byte[] serialize(Object object) {
        return Kryos.serialize(object);
    }

    @Override
    public Object deserialize(byte[] data, Class aClass) {
        return Kryos.deserialize(data,aClass);
    }
}
