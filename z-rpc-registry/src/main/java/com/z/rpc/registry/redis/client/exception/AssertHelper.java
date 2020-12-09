package com.z.rpc.registry.redis.client.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * <b>AssertHelper.java</b></br>
 * 
 * <pre>
 * 断言工具类
 * </pre>
 */
public class AssertHelper {
	
	/**
	 * 
	 * <b>notNull </b> <br/>
	 * 
	 * When the param "object" is null ,Then throw new RedisClientException(message). <br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param object
	 * @param message
	 *            void
	 *
	 */
	public static void notNull(Object object, String message) {
		if (null == object) {
			throw new RedisClientException(message);
		}
	}
	
	/**
	 * 
	 * <b>notBlank </b> <br/>
	 * 
	 * if a CharSequence is whitespace, empty ("") or null Then "throw new
	 * RedisClientException(message)".<br/>
	 * 
	 * <pre>
	 * AssertHelper.notBlank(null)      = throw new RedisClientException(message)
	 * AssertHelper.notBlank("")        = throw new RedisClientException(message)
	 * AssertHelper.notBlank(" ")       = throw new RedisClientException(message)
	 * AssertHelper.notBlank("bob")     = true
	 * AssertHelper.notBlank("  bob  ") = true
	 * </pre>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param cs
	 * @param message
	 *            void
	 *
	 */
	public static boolean notBlank(CharSequence cs, String message) {
		if (StringUtils.isBlank(cs)) {
			throw new RedisClientException(message);
		}
		return true;
	}
	
	/**
	 * 
	 * <b>isTrue </b> <br/>
	 * 
	 * When the param "isTrue" is flase ,Then throw new RedisClientException(message). <br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param isTrue
	 * @param message
	 *            void
	 *
	 */
	public static void isTrue(boolean isTrue, String message) {
		if (!isTrue) {
			throw new RedisClientException(message);
		}
	}
	
}
