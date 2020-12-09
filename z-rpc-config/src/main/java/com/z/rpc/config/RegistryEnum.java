/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.config;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/24
 */
public enum  RegistryEnum {

    ZOOKEEPER("ZOOKEEPER"),
    REDIS("REDIS");

    private String type;

    RegistryEnum(String type){
        this.type=type;
    }
}
