package com.milesbone.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;

import com.milesbone.cache.redis.service.IRedisCache;
import com.milesbone.common.test.base.AbstactServerBeanTestCase;


public class RedisClusterBeanTest extends AbstactServerBeanTestCase {

	@Resource(name="redisCache")
	private IRedisCache cache;

	public void setup() {
		logger.debug("setup begin.....");
		
	}
	
	@Test
	public void testInitial(){
		logger.debug("hello ");
		System.out.println(cache.contains("test") );
	}

	
	@Test
	public void testSave(){
		String key = "test";
		if(!cache.contains(key)){
			cache.putCache(key, "this is a test");
		}
	}
	
	@Test
	public void testgetCluster(){
		logger.debug(cache.getCache("test"));
	}
	
	
	
	@Test
	public void testSaveOrUpdate(){
		String key = "test";
		cache.putOrReplace(key, "this is a test for testSaveOrUpdate");
	}
	
	@Test
	public void testDel(){
		String key = "test";
		cache.remove(key);
	}
	
	@Test
	public void testGet(){
		String key = "test";
		if(cache.contains(key)){
			logger.info("获取redis服务key:{},value:{}",key, cache.getCache(key));
		}else{
			logger.info("未找到相应redis的key");
		}
	}
	
	
	@Test
	public void testTTL(){
		String key = "test";
		if(cache.contains(key)){
			
			logger.info("获取redis服务key:{},value:{},ttl:{}",key, cache.getCache(key),cache.ttl(key));
		}else{
			logger.info("未找到相应redis的key");
		}
	}

	public void tearDown() {
//		cache.close();
		logger.debug("Method done.....");
	}
	
}
