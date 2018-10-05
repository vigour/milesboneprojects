package com.milesbone.cache.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.common.monitor.CacheExpireMonitor;
import com.milesbone.cache.local.entity.LocalCacheEntity;
import com.milesbone.cache.local.service.impl.LocalCacheImpl;

public class LocalCacheServiceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(LocalCacheServiceTest.class);

	@Test
	public void testCacheServiceImpl() {
		LocalCacheImpl cacheImpl = new LocalCacheImpl();
		cacheImpl.putCache("test", "test", 10 * 1000L);
		cacheImpl.putCache("myTest", "myTest", 15 * 1000L);
		CacheExpireMonitor cacheListener = new CacheExpireMonitor(cacheImpl);
		cacheListener.run();
		LocalCacheEntity localcache = (LocalCacheEntity) cacheImpl.getCache("test");
		logger.info("test:{}" , localcache.getValue());
		localcache = (LocalCacheEntity) cacheImpl.getCache("myTest");
		logger.info("myTest:{}",  localcache.getValue());
		try {
			TimeUnit.SECONDS.sleep(20);        
		} catch (InterruptedException e) {
			e.printStackTrace();        
		}
		logger.info("test:" + cacheImpl.getCache("test"));
		logger.info("myTest:" + cacheImpl.getCache("myTest"));
	}
	
	
	/**
	 * 线程安全
	 */
	@Test    
	public void testThredSafe() {
		final String key = "thread";
		final LocalCacheImpl cacheImpl = new LocalCacheImpl();
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 100; i++) { 
			exec.execute(new Runnable() {
				public void run() {
					if (!cacheImpl.contains(key)) {
						cacheImpl.putCache(key, 1, 0);
					} else {
						//因为+1和赋值操作不是原子性的，所以把它用synchronize块包起来
						synchronized (cacheImpl) {
							int value = (Integer) cacheImpl.getCache(key) + 1; 
							cacheImpl.putCache(key,value , 0);
						}
					}
				}
			});
		}
		exec.shutdown();
		try {
			exec.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		logger.info(cacheImpl.getCache(key).toString());
	}
}
