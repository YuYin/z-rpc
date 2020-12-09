package com.z.rpc.common.pool;


import com.z.rpc.common.pool.impl.DefaultPoolFactory;

import java.io.Closeable;


public abstract class Pool<T> implements Closeable { 
	
	public abstract T borrowObject() throws Exception;
	
	public abstract void returnObject(T obj);

	public static <T> Pool<T> getPool(ObjectFactory<T> factory, PoolConfig config){
		return Pool.factory.getPool(factory, config);
	}
	
	
	private static PoolFactory factory;
	
	static {
		initDefaultFactory();
	} 
	
	public static void setPoolFactory(PoolFactory factory) {
		if (factory != null) {
			Pool.factory = factory;
		}
	}
	
	public static void initDefaultFactory() {
		if (factory != null){
			return ;
		}
		String defaultFactory = String.format("%s.impl.CommonsPool2Factory", Pool.class.getPackage().getName());
		try {
			//try commons-pool2
			Class.forName("org.apache.commons.pool2.BasePooledObjectFactory");
			Class<?> factoryClass = Class.forName(defaultFactory);
			factory = (PoolFactory)factoryClass.newInstance();
		} catch (Exception e) { 
			factory = new DefaultPoolFactory();
		}
	}
}
