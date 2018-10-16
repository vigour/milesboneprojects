package com.milesbone.cache.local;

import com.milesbone.common.config.IConfiguration;


/**
 * 
 * @Title  ILocalCacheConfig.java
 * @Package com.milesbone.cache.local
 * @Description    本地缓存接口
 * @author miles
 * @date   2018-09-27 10:36
 */
public interface ILocalCacheConfig extends IConfiguration{
	
	/**
	 * 获取默认缓存大小
	 * @return
	 */
	public long getDefaultCapacity();
	
	/**
	 * 获取默认缓存失效时间
	 * @return
	 */
	public long getDefaultExpireTime();
	

	/**
	 * 获取缓存最大容量值
	 * @return
	 */
	public long getMaxCapacity();
}
