package com.z.rpc.registry.redis.client.event;

/**
 * <b>RedisListener.java</b></br>
 * 
 * <pre>
 * 自定义的发布订阅接口类
 * </pre>
 */
public interface RedisListener {
	
	/**
	 * 
	 * <b>onMessage</b> <br/>
	 * <br/>
	 * 
	 * 订阅消息 <br/>
	 * 
	 * @author cpthack cpt@jianzhimao.com
	 * @param channel
	 * @param message
	 *            void
	 *
	 */
	void onMessage(String channel, String message);
}
