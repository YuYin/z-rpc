package com.z.rpc.registry.redis.client.core;

import com.z.rpc.registry.redis.client.config.RedisConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * <b>JedisPoolFactory.java</b></br>
 */
public class JedisPoolFactory {
	private static Logger logger        = LoggerFactory
	                                                            .getLogger(JedisPoolFactory.class);
	private static RedisConfig            configDefault = new RedisConfig();
	private static Map<String, JedisPool> mapConfigPool = new ConcurrentHashMap<String, JedisPool>();
	
	// 禁止实例化
	private JedisPoolFactory() {
	}
	
	private static JedisPool getConfigPool(String key) {
		JedisPool pool = mapConfigPool.get(key);
		return pool;
	}
	
	private synchronized static boolean initConfigPool(RedisConfig config) {
		if (config == null) {
			return false;
		}
		String key = config.getConfigFile();
		JedisPool jedisPool = getConfigPool(key);
		if (jedisPool != null) {
			return true;
		}
		String server = config.getServerIp();
		int port = config.getServerPort();
		String auth = config.getServerAuth();
		boolean isAuth = false;
		if (StringUtils.isNotBlank(auth)) {
			isAuth = true;
		}
		logger.warn("The redis server:" + server + ":" + port);
		if (StringUtils.isBlank(server)) {
			return false;
		}
		// 池基本配置
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(config.getPoolMax());
		poolConfig.setMaxIdle(config.getPoolIdle());
		poolConfig.setMaxWaitMillis(config.getTryTimeout());
		poolConfig.setTestOnBorrow(config.getTestOnBorrow());
		int timeout = config.getTryTimeout();// msec
		
		if (isAuth) {
			jedisPool = new JedisPool(poolConfig, server, port, timeout, auth);
		}
		else {
			jedisPool = new JedisPool(poolConfig, server, port, timeout);
		}
		if (jedisPool != null) {
			mapConfigPool.put(key, jedisPool);
		}
		return jedisPool != null;
	}
	
	// 获取默认的Redis连接
	public static Jedis getClient() {
		return getClient(configDefault);
	}
	
	// 获取指定配置的Redis连接
	public static Jedis getClient(RedisConfig config) {
		if (config == null) {
			return null;
		}
		String key = config.getConfigFile();
		JedisPool jedisPool = getConfigPool(key);
		if (jedisPool == null) {
			initConfigPool(config);
			jedisPool = getConfigPool(key);
		}
		if (jedisPool == null) {
			return null;
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
		}
		catch (Exception e) {
			logger.warn("获取指定配置的redis连接失败，Error:", e);
		}
		if (jedis == null) {
			logger.warn("Cannot get Jedis object from the pool!");
		}
		return jedis;
	}
}
