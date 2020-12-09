package com.z.rpc.samples.server;

import com.z.rpc.config.ProviderConfig;
import com.z.rpc.registry.zookeeper.ZookeeperRegistry;
import com.z.rpc.server.AbstractRemoteServer;
import com.z.rpc.server.RPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(SimpleServerStarter.class);

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port=18866;
        String serviceScanPackages="com.z.rpc.samples";
        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry("192.168.32.54:2181");
     //   AbstractRemoteServer abstractRemoteServer = new TCPRPCServer(ip,port, zookeeperRegistry, ProviderConfig.rpcSerializerProtocol,serviceScanPackages);
        AbstractRemoteServer abstractRemoteServer = new RPCServer(ip,port, zookeeperRegistry, ProviderConfig.rpcSerializerProtocol,serviceScanPackages);

        // HelloService helloService = new HelloServiceImpl();
        //PersonService personService=new PersonServiceImpl();
       // remoteServer.loadService("com.zds.rpc.samples.client.HelloService", helloService);
        //remoteServer.loadService("com.zds.rpc.samples.client.PersonService",personService);
        try {
            abstractRemoteServer.startup();
        } catch (Exception ex) {
            logger.error("Exception: {}", ex);
        }
    }
}
