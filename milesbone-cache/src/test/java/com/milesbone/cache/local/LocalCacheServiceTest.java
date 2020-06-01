package com.milesbone.cache.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.local.entity.LocalCacheEntity;
import com.milesbone.cache.local.service.ILocalCache;
import com.milesbone.common.test.base.AbstactBeanTestCase;

public class LocalCacheServiceTest extends AbstactBeanTestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(LocalCacheServiceTest.class);

	@Resource(name="localCache")
	private ILocalCache cacheImpl;
	
	@Test
	public void testCacheServiceImpl() {
		cacheImpl.putCache("test", "test", 7L);
		cacheImpl.putCache("myTest", "myTest", 15L);
		cacheImpl.putCache("NonExpireTest", "NonExpireTest", 0L);
		
		LocalCacheEntity localcache = (LocalCacheEntity) cacheImpl.getCacheEntity("test");
		logger.info("test:{}" , localcache.getValue());
		localcache = (LocalCacheEntity) cacheImpl.getCacheEntity("myTest");
		logger.info("myTest:{}",  localcache.getValue());
		localcache = (LocalCacheEntity) cacheImpl.getCacheEntity("NonExpireTest");
		logger.info("NonExpireTest:{}",  localcache.getValue());
		
		try {
			TimeUnit.SECONDS.sleep(10); 
			logger.info("test:" + cacheImpl.getCache("test"));
			logger.info("myTest:" + cacheImpl.getCache("myTest"));
			logger.info("NonExpireTest:" + cacheImpl.getCache("NonExpireTest"));
		} catch (InterruptedException e) {
			e.printStackTrace();        
		}
		try {
			TimeUnit.SECONDS.sleep(10); 
			logger.info("test:" + cacheImpl.getCache("test"));
			logger.info("myTest:" + cacheImpl.getCache("myTest"));
			logger.info("NonExpireTest:" + cacheImpl.getCache("NonExpireTest"));
		} catch (InterruptedException e) {
			e.printStackTrace();        
		}
	}
	
	
	/**
	 * 线程安全
	 */
	@Test    
	public void testThredSafe() {
		final String key = "thread";
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


	public void setup() {
		logger.debug("setup begin.....");
	}


	public void tearDown() {
		logger.debug("done!!!!!");		
	}
	
	
}
