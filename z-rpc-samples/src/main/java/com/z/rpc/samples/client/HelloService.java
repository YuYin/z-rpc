package com.z.rpc.samples.client;

public interface HelloService {
    String hello(String name);

    String hello(Person person);
}
