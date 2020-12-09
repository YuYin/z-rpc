package com.z.rpc.common.pool.impl;

import com.z.rpc.common.pool.PoolConfig;
import com.z.rpc.common.pool.PoolFactory;
import com.z.rpc.common.pool.ObjectFactory;
import com.z.rpc.common.pool.Pool;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonsPool2Factory implements PoolFactory {
	private static final Logger log = LoggerFactory.getLogger(Pool.class);
	@Override
	public <T> Pool<T> getPool(ObjectFactory<T> factory, PoolConfig config) {
		log.debug("Using Apache Commons-pool2");
		return new CommonsPool2<T>(factory, config);
	}
}

class CommonsPool2<T> extends Pool<T> {
	private GenericObjectPool<T> support;
	
	public CommonsPool2(ObjectFactory<T> supportFactory, PoolConfig config) {   
		Commons2PoolFactory factory = new Commons2PoolFactory(supportFactory); 
		GenericObjectPoolConfig poolConfig = null;
		if(config.getSupport() instanceof GenericObjectPoolConfig){
			poolConfig = (GenericObjectPoolConfig)config.getSupport();
		} else {
			poolConfig = new GenericObjectPoolConfig();
			poolConfig.setMaxTotal(config.getMaxTotal());
			poolConfig.setMaxIdle(config.getMaxIdle());
			poolConfig.setMinIdle(config.getMinIdle());
			poolConfig.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
		}
		
		this.support = new GenericObjectPool<T>(factory, poolConfig);
	}

	@Override
	public T borrowObject() throws Exception { 
		return support.borrowObject();
	}

	@Override
	public void returnObject(T obj){ 
		support.returnObject(obj);
	}

	@Override
	public void close() { 
		support.close();
	}  
	
	private class Commons2PoolFactory extends BasePooledObjectFactory<T> {
		ObjectFactory<T> support; 
		
		public Commons2PoolFactory(ObjectFactory<T> support){ 
			this.support = support;
		}
		
		@Override
		public T create() throws Exception { 
			return support.createObject();
		}
		
		@Override
		public PooledObject<T> wrap(T obj) {
			return new DefaultPooledObject<T>(obj);
		}
		
		@Override
		public void destroyObject(PooledObject<T> p) throws Exception {
			T obj = p.getObject();
			support.destroyObject(obj);
		}
		
		@Override
		public boolean validateObject(PooledObject<T> p) {
			return support.validateObject(p.getObject());
		}
	}
}