package com.milesbone.cache.ehcache.service.impl;

import java.util.Set;

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
public class EHCacheServiceImpl implements IEHCache{
	
	private CacheManager cacheManager;


	public Cache getEHCache(String cacheName) {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean putCache(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean putCache(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean putOrReplace(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean putOrReplace(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean replace(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean replace(String key, String value, long time) {
		// TODO Auto-generated method stub
		return false;
	}


	public String getCache(String key) {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean remove(String key) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean clearAll() {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean contains(String key) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean isExpired(String key) {
		// TODO Auto-generated method stub
		return false;
	}


	public Set<String> getAllKeys() {
		// TODO Auto-generated method stub
		return null;
	}


}
