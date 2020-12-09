package com.z.rpc.samples.server;


import com.z.rpc.samples.client.HelloService;
import com.z.rpc.samples.client.Person;
import com.z.rpc.server.RpcService;

@RpcService(interfaceClass = HelloService.class)
public class HelloServiceImpl implements HelloService {

    public HelloServiceImpl(){

    }

    @Override
    public String hello(String name) {
        throw new RuntimeException("xxx");
       // return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
  /*      try {
            //验证超时重试
            Thread.sleep(40000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
