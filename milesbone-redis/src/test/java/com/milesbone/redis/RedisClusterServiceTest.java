package com.milesbone.redis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.redis.service.IRedisCache;
import com.milesbone.redis.service.impl.RedisClusterImpl;

import junit.framework.TestCase;

public class RedisClusterServiceTest extends TestCase{

	private static final Logger logger = LoggerFactory.getLogger(RedisClusterServiceTest.class);
	private IRedisCache cache;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = new RedisClusterImpl();
	}
	
	@Test
	public void testSave(){
		String key = "test";
		if(!cache.exist(key)){
			cache.save(key, "this is a test");
		}
	}
	
	@Test
	public void testSaveOrUpdate(){
		String key = "test";
		cache.saveOrUpdate(key, "this is a test for testSaveOrUpdate");
	}
	
	@Test
	public void testDel(){
		String key = "test";
		cache.remove(key);
	}
	
	@Test
	public void testGet(){
		String key = "test";
		if(cache.exist(key)){
			logger.info("获取redis服务key:{},value:{}",key, cache.get(key));
		}else{
			logger.info("未找到相应redis的key");
		}
	}
	
	
}
