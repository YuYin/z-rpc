/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.server;

import com.z.rpc.common.Constants;
import com.z.rpc.common.filter.Filter;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/8/26
 */
public abstract class BaseProviderFilter implements Filter{

    @Override
    public String group() {
        return Constants.PROVIDER;
    }
}
