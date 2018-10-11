package com.milesbone.cache.ehcache.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.InitializingBean;

import net.sf.ehcache.Cache;


/**
 * 缓存结束返回的切面
 * @Title  CacheAfterReturningAdvice.java
 * @Package com.milesbone.cache.ehcache.interceptor
 * @Description    TODO
 * @author miles
 * @date   2018-10-09 14:19
 */
public class CacheAfterReturningAdvice implements AfterReturningAdvice, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(CacheAfterReturningAdvice.class);
	
	private Cache cache;
	

	public CacheAfterReturningAdvice() {
		super();
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void afterPropertiesSet() throws Exception {
		if (cache == null) {
			logger.error("缓存值为空需要创建新缓存");
			throw new IllegalArgumentException("Need a cache. Please use setCache(Cache) create it.");
		}
	}

	/**
	 * 
	 * 数据返回的切面
	 * @param returnValue the value returned by the method, if any
	 * @param method method being invoked
	 * @param args arguments to the method
	 * @param target target of the method invocation. May be {@code null}.
	 * @throws Throwable if this object wishes to abort the call.
	 * 
	 */
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		String className = target.getClass().getName();
		List<?> list = cache.getKeys();
		String cachekey = null;
		for(int i=0; i < list.size(); i++) {
			cachekey = String.valueOf(list.get(i));
			if(cachekey.startsWith(className)) {
				cache.remove(cachekey);
				logger.debug("清除缓存{}....",cachekey);
			}
		}
		
	}

}
