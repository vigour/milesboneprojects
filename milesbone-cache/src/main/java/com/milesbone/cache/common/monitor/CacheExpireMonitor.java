package com.milesbone.cache.common.monitor;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.cache.ICache;

/**
 * 缓存过期监控的线程
 * @Title  CacheExpireMonitor.java
 * @Package com.milesbone.cache.local.monitor
 * @Description    TODO
 * @author miles
 * @date   2018-10-05 09:12
 */
public class CacheExpireMonitor implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(CacheExpireMonitor.class);
	
	
	
	private ICache<Object> caches;
	
	public CacheExpireMonitor() {
		this(null);
	}
	
	public CacheExpireMonitor(ICache<Object> caches) {
		super();
		this.caches = caches;
	}

	public ICache<Object> getCaches() {
		return caches;
	}

	public void setCaches(ICache<Object> caches) {
		this.caches = caches;
	}

	public void run() {
		logger.info("过期缓存监控线程启动...");
		if(caches != null) {
			while (true) {
				for(String key : caches.getAllKeys()) {
					if(caches.isExpired(key)) {
						caches.remove(key);
						logger.debug("缓存key:{}已过期", key);
					}
				}
			}
		}
		logger.info("过期缓存监控线程启动完成");
	}


}
