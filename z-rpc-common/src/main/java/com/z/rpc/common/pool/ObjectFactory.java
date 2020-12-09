package com.z.rpc.common.pool;

public interface ObjectFactory<T> {
	
	T createObject() throws Exception;
	
	void destroyObject(T obj);
	
	boolean validateObject(T obj);
}
