package com.milesbone.cache.redis;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.redis.service.IRedisCache;
import com.milesbone.cache.redis.service.impl.RedisClusterCacheImpl;

import junit.framework.TestCase;

public class RedisClusterServiceTest extends TestCase{

	private static final Logger logger = LoggerFactory.getLogger(RedisClusterServiceTest.class);
	
	@Resource(name="redisCache")
	private IRedisCache cache;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cache = new RedisClusterCacheImpl();
	}
	
	
	@Test
	public void testgetCluster(){
		RedisClusterCacheImpl cluster = new RedisClusterCacheImpl();
		logger.debug(cluster.getJedisCluster().toString());
	}
	
	@Test
	public void testSave(){
		String key = "test";
		if(!cache.contains(key)){
			cache.putCache(key, "this is a test");
		}
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
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
