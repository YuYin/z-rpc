package com.z.rpc.samples.client;


import com.z.rpc.client.AbstractRpcClient;
import com.z.rpc.client.RpcClient;
import com.z.rpc.client.loadbalance.LoadBalanceStrategy;
import com.z.rpc.common.CallType;
import com.z.rpc.common.serialize.RpcSerializerProtocol;
import com.z.rpc.config.ConsumerConfig;
import com.z.rpc.registry.Registry;
import com.z.rpc.registry.zookeeper.ZookeeperRegistry;

import java.util.concurrent.CountDownLatch;

public class PersonCallbackSimpleClientTest {
    public static void main(String[] args) throws Exception {
        
        String registryAddress = "192.168.32.54:2181";
        Registry registry = new ZookeeperRegistry(registryAddress);
        LoadBalanceStrategy loadBalanceStrategy = LoadBalanceStrategy.RANDOM;
       AbstractRpcClient rpcClient = new RpcClient(registry,RpcSerializerProtocol.HESSIANSERIALIZE);
       rpcClient.startup();
       
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(10 * 1000L);
        consumerConfig.setCallType(CallType.CALLBACK);
        
        PersonService personService = rpcClient.proxy(PersonService.class, consumerConfig, loadBalanceStrategy);
        for (int i = 0; i < 100; i++) {
            personService.getTestPerson("xxxxxxxxxx", 5);
            //增加异步回调获取结果
        /*    InvokeFuture.getFuture().addCallback(new AsyncRPCCallback() {
                @Override
                public void success(Object result) {
                    List<Person> persons = (List<Person>) result;
                    for (int i = 0; i < persons.size(); ++i) {
                        System.out.println(persons.get(i));
                    }
                      System.out.println();
                    countDownLatch.countDown();
                }

                @Override
                public void fail(Exception e) {
                    System.out.println(e);
                    countDownLatch.countDown();
                }
            });*/
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End");
    }
}
