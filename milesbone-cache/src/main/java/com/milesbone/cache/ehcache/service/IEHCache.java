package com.milesbone.cache.ehcache.service;


import com.milesbone.common.cache.ICache;

import net.sf.ehcache.Cache;


/**
 * 
 * @Title  IEHCache.java
 * @Package com.milesbone.cache.ehcache.service
 * @Description    继承自ICache接口
 * @author miles
 * @date   2018-09-26 17:55
 */
public interface IEHCache extends ICache {

	/**
	 * 根据cache名称获取EhCache对象
	 * @param cache
	 * @return
	 */
	public Cache getCache(String  cache); 
}
