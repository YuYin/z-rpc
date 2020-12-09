package com.z.rpc.client.hystrix;


/**
 * 业务失败返回处理函数
 */
public interface Fallback {
  <R>  R invoke();
}
