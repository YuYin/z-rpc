/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.registry;

import java.util.List;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/31
 */
public interface DataChangeCallBack {
    void call(List<ServerNode> serverNodeList);
}
