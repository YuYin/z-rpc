package com.z.rpc.common.pool;

public interface PoolFactory {
	<T> Pool<T> getPool(ObjectFactory<T> factory, PoolConfig config);
}
