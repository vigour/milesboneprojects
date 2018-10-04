package com.milesbone.cache.ehcache.service.impl;

import com.milesbone.cache.ehcache.service.IEHCache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * 
 * @Title  EHCacheServiceImpl.java
 * @Package com.milesbone.cache.ehcache.service.impl
 * @Description    EHCache 缓存实现类
 * @author miles
 * @date   2018-09-27 15:20
 */
public class EHCacheServiceImpl implements IEHCache {
	
	private CacheManager cacheManager;

	public boolean save(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean save(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean saveOrUpdate(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean saveOrUpdate(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}

	public String get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean exist(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public Cache getCache(String cache) {
		// TODO Auto-generated method stub
		return null;
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

}
