package com.z.rpc.registry;

/**
 * ZooKeeper constant
 *
 */
public interface Constant {

      int ZK_SESSION_TIMEOUT = 5000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";

     String REDIS_REGISTRY_SERVICE_KEY="redis_registry_service_key";

     String REDIS_SERVICE_CHANGE_CHANNEL="redis_service_change_channel";
}
