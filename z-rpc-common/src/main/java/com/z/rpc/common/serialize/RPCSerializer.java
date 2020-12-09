/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.common.serialize;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/22
 */
public interface RPCSerializer {
     byte[] serialize(Object object);

     Object deserialize(byte[] data,Class aClass);
}
