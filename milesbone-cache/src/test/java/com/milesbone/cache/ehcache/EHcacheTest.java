package com.milesbone.cache.ehcache;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.ehcache.service.IEHCache;
import com.milesbone.common.test.base.AbstactBeanTestCase;

import net.sf.ehcache.Cache;



public class EHcacheTest extends AbstactBeanTestCase {

	private static final Logger logger = LoggerFactory.getLogger(EHcacheTest.class);
	
	@Resource(name="ehcacheService")
	private IEHCache service;
	
	
	public void setup() {
		logger.info("Ehcache initial start.....");
		
	}

	
	@Test
	public void testGetEhcache() {
		Cache cache = service.getEHCache("test");
		logger.info("cache name:{}",cache.getName());
	}
	
	
	
	@Test
	public void testPutCache() {
		String key = "key1";
		//自定义cache
		logger.debug("缓存开始赋值");
		for(int i=0; i<100; i++) {
			service.putCache("key"+i, "value"+i);
		}
		service.put(key, "value a");
		logger.debug("缓存赋值结束..");
		
		Object obj = service.getCacheValue("basecache", key);
		logger.info("查询缓存{} = value:{}", key, obj);
	}
	
	
	
	@Test
	public void testPut() {
		String key = "key1";
		logger.debug("缓存开始赋值");
		for(int i=0; i<100; i++) {
			service.put("key"+i, "value"+i);
		}
		logger.debug("缓存赋值结束..");
		org.springframework.cache.Cache.ValueWrapper vw = service.get(key);
		logger.info("ValueWrapper value:{}", vw.get());
	}
	
	@Test
	public void testGetCache() {
		org.springframework.cache.Cache.ValueWrapper vw = service.get("key1");
		logger.info("ValueWrapper value:{}", vw);
	}
	
	
	public void tearDown() {
		logger.info("ehcache test finish.");
	}

}
