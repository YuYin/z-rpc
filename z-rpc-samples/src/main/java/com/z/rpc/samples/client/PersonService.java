package com.z.rpc.samples.client;

import java.util.List;

public interface PersonService {
    List<Person> getTestPerson(String name, int num);
}
