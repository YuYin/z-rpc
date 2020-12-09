package com.z.rpc.samples.server;


import com.z.rpc.samples.client.Person;
import com.z.rpc.samples.client.PersonService;
import com.z.rpc.server.RpcService;

import java.util.ArrayList;
import java.util.List;
@RpcService(interfaceClass = PersonService.class)
public class PersonServiceImpl implements PersonService {

    @Override
    public List<Person> getTestPerson(String name, int num) {
        List<Person> persons = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            persons.add(new Person(Integer.toString(i), name));
        }
        return persons;
    }
}
