package com.milesbone.cache.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.milesbone.common.cache.ICache;


/**
 * Redis缓存接口和Hash缓存
 * @author miles
 * @date 2017-03-26 下午9:27:46
 */
public interface IRedisCache extends ICache{
	
	
	/**
	 * 向名称为key的hash中添加元素field<—>value
	 * @param key
	 * @param map
	 * @return
	 */
	public boolean hset(String key,String field,String value);
	
	/**
	 * 返回名称为key的hash中field对应的value
	 * @param key
	 * @param fields
	 * @return
	 */
	public String hget(String key,String field);
	
	/**
	 * 名称为key的hash中是否存在键为field的域
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hexists(String key, String field);
	
	/**
	 * 删除名称为key的hash中键为field的域
	 * @param key
	 * @param field
	 * @return 
	 */
	public long hdel(String key, String field);
	
	/**
	 * 返回名称为key的hash中元素个数
	 * @param key
	 * @return
	 */
	public long hlen(String key);
	
	/**
	 * 返回名称为key的hash中所有键 field
	 * @param key
	 * @return
	 */
	public Set<String> hkeys(String key);
	
	/**
	 * 返回名称为key的hash中所有键（field）对应的value
	 * @param key
	 * @return
	 */
	public List<String> hvals(String key);
	
	/**
	 * 返回名称为key的hash中所有的键（field）及其对应的value
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(String key);
	
	
	
	boolean hset(byte[] key, byte[] field, byte[] value);

	byte[] hget(byte[] key, byte[] field);

	boolean hexists(byte[] key, byte[] field);

	long hdel(byte[] key, byte[] field);

	long hlen(byte[] key);

	Set<byte[]> hkeys(byte[] key);

	List<byte[]> hvals(byte[] key);

	Map<byte[], byte[]> hgetAll(byte[] key);

	/**
	 * 根据key删除redis缓存  
	 * @param key
	 * @return
	 */
	boolean remove(byte[] key);
	
	/**
	 * 获取key的生存时间
	 * @param key
	 * @return
	 */
	public Long ttl(String key);
	
	/**
	 * 获取key的生存时间
	 * @param key
	 * @return
	 */
	public Long ttl(byte[] key);

	/**
	 * 使key失效
	 * @param key
	 * @param seconds
	 */
	public void expire(String key, int seconds);
	
	/**
	 * 使key失效
	 * @param key
	 * @param seconds
	 */
	public void expire(byte[] key, int seconds);
	
	
	/**
	 * 关闭jediscluster
	 */
	public void close();
	
}
