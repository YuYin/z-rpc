package com.z.rpc.server;

import java.lang.annotation.*;

/**
 * RPC annotation for RPC service
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {
    Class<?> interfaceClass()default void.class;
}
