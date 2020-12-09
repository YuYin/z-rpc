/*
 *
 * Copyright (c) 2005-2017 All Rights Reserved.
 */
package com.z.rpc.samples.client.spring;

import com.z.rpc.client.RpcReference;
import com.z.rpc.samples.client.HelloService;
import com.z.rpc.samples.client.Person;
import com.z.rpc.samples.client.PersonService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href=mailto:someharder@gmail.com>yinyu</a> 2020/7/21
 */
@Component(value = "rpcClientComponent")
public class RPCClientComponent {

     @RpcReference(timeout = 10*1000)
    private HelloService helloService;
     @RpcReference(timeout=30*1000)
     private PersonService personService;


     public String doHelloWorldTest(String xxx){
      return helloService.hello(xxx);
     }
     public List<Person> doPersonTest(String name, int num){
         return personService.getTestPerson(name,num);
     }

}
