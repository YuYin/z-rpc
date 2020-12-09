package com.z.rpc.common.pool;

public class PoolConfig implements Cloneable{ 
	private int maxTotal = 64;
    private int maxIdle = 64;
    private int minIdle = 64;
    private long minEvictableIdleTimeMillis = 1000L * 60L * 30L;
    private Object support;
	
    public int getMaxTotal() {
		return maxTotal;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}
	public Object getSupport() {
		return support;
	}
	public void setSupport(Object support) {
		this.support = support;
	}   
}
