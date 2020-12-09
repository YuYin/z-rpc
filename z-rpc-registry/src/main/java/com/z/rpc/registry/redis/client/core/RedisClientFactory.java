package com.z.rpc.registry.redis.client.core;

import com.z.rpc.registry.redis.client.config.RedisConstants;
import com.z.rpc.registry.redis.client.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>RedisClientFactory.java</b></br>
 * <pre>
 * RedisClient对象生成工厂类
 * </pre>
 */
public class RedisClientFactory {
	private static Logger logger         = LoggerFactory.getLogger(RedisClientFactory.class);
	private static Map<String, RedisClient> redisClientMap = new ConcurrentHashMap<String, RedisClient>();
	
	/**
	 * 
	 * <b>获取RedisClient对象</b> <br/>
	 * <br/>
	 * 
	 * 获取默认RedisConfig配置的RedisClient对象<br/>
	 * 
	 */
	public static RedisClient getClient() {
		return getClient(null);
	}
	
	public static RedisClient getClient(RedisConfig redisConfig) {
		RedisClient redisClient = null;
		String redisConfigFileName = RedisConstants.DEFALUT_REDIS_FILE_NAME;
		
		// 获取默认RedisConfig配置的RedisClient对象，并缓存到redisClientMap对象中
		if (null == redisConfig) {
			redisClient = redisClientMap.get(redisConfigFileName);
			if (redisClient == null) {
				redisClient = new JedisRedisClient().setRedisConfig(null);
				redisClientMap.put(redisConfigFileName, redisClient);
			}
			return redisClient;
		}
		
		// 获取自定义的RedisConfig配置的RedisClient对象，并缓存到redisClientMap对象中
		redisConfigFileName = redisConfig.getConfigFile();
		redisClient = redisClientMap.get(redisConfigFileName);
		if (redisClient == null) {
			redisClient = new JedisRedisClient().setRedisConfig(redisConfig);
			redisClientMap.put(redisConfigFileName, redisClient);
		}
		return redisClient;
	}
}
