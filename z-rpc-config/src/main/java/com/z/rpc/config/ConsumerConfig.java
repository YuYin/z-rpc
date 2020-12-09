/*
 * www.zdsoft.cn Inc.
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.config;

import com.z.rpc.common.CallType;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/23
 */
public class ConsumerConfig {

  private long timeout;

  //直连url
  private String directUrl;
  

   private CallType callType=CallType.SYNC;

   private int retries;

   private boolean enableHystrix=false;

   private String fallBackClassName;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public String getDirectUrl() {
        return directUrl;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public boolean isEnableHystrix() {
        return enableHystrix;
    }

    public void setEnableHystrix(boolean enableHystrix) {
        this.enableHystrix = enableHystrix;
    }

    public String getFallBackClassName() {
        return fallBackClassName;
    }

    public void setFallBackClassName(String fallBackClassName) {
        this.fallBackClassName = fallBackClassName;
    }
}
