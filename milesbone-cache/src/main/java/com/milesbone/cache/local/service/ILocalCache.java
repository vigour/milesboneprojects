package com.milesbone.cache.local.service;

import com.milesbone.cache.common.entity.ICacheEntity;
import com.milesbone.common.cache.ICache;


/**
 * 
 * @Title  ILocalCache.java
 * @Package com.milesbone.cache.local
 * @Description    本地缓存接口
 * @author miles
 * @date   2018-09-27 10:24
 */
public interface ILocalCache extends ICache<Object>{

	
	/**
	 * 缓存赋值
	 * @param key
	 * @param entity
	 * @return
	 */
	public boolean putCache(String key, ICacheEntity entity);
	
	
	/**
	 * 缓存赋值
	 * @param key
	 * @param entity
	 * @return
	 */
	public ICacheEntity getCacheEntity(String key);
}
