package com.milesbone.cache.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.redis.IRedisConfig;
import com.milesbone.cache.redis.config.DefaultRedisConfig;
import com.milesbone.cache.redis.service.IRedisCache;

import redis.clients.jedis.JedisCluster;

/**
 * redis集群实现
 * 
 * @author miles
 * @date 2017-03-26 下午11:51:33
 */
public class RedisClusterCacheImpl implements IRedisCache {

	private static final Logger logger = LoggerFactory.getLogger(RedisClusterCacheImpl.class);

	/**
	 * jedis集群操作对象
	 */
	private JedisCluster jedisCluster;
	
	/**
	 * 设置默认数据有效期 ，默认30天（2592000秒）
	 */
	private Integer dataExpireTime;

	/**
	 *  
	 * 
	 */
	public RedisClusterCacheImpl() {
		this(null);
	}

	/**
	 * @param jedisCluster
	 */
	public RedisClusterCacheImpl(JedisCluster jedisCluster) {
		this(jedisCluster, 0);
	}

	
	
	
	/**
	 * @param jedisCluster
	 * @param dataExpireTime
	 */
	public RedisClusterCacheImpl(JedisCluster jedisCluster, Integer dataExpireTime) {
		super();
		logger.debug("RedisClusterCache 构造方法开始");
		
		IRedisConfig redisConfig = new DefaultRedisConfig();
		
		if(dataExpireTime == null || dataExpireTime == 0){
			dataExpireTime = redisConfig.getDataExpireTime();
		}
		this.dataExpireTime = dataExpireTime;
		
		if (jedisCluster == null) {
			ReentrantLock lock = new ReentrantLock();
			lock.lock();
			logger.debug("redis 集群初始化开始...");
			jedisCluster = new JedisCluster(redisConfig.getNodes(), redisConfig.getConnectionTimeout(),
					redisConfig.getMaxAttempts(), redisConfig.getPoolconfig());
			logger.debug("redis 集群初始化完成");
			lock.unlock();
		}
		this.jedisCluster = jedisCluster;
		
		logger.debug("redis 构造方法完成...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#putCache(java.lang.String,
	 * java.lang.String)
	 */
	public boolean putCache(String key, String value) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, dataExpireTime, value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#putCache(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean putCache(String key, String value, long expireTime) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, new Long(expireTime).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#putOrReplace(java.lang.String,
	 * java.lang.String)
	 */

	public boolean putOrReplace(String key, String value) {
		return this.jedisCluster.setex(key, dataExpireTime, value).equals("OK") ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#putOrReplace(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean putOrReplace(String key, String value, long expireTime) {
		return this.jedisCluster.setex(key, new Long(expireTime).intValue(), value).equals("OK") ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#replace(java.lang.String,
	 * java.lang.String)
	 */

	public boolean replace(String key, String value) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.set(key, value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#replace(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean replace(String key, String value, long expireTime) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, new Long(expireTime).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#getCache(java.lang.String)
	 */

	public String getCache(String key) {
		return this.jedisCluster.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#remove(java.lang.String)
	 */

	public boolean remove(String key) {
		return this.jedisCluster.del(key) == 1L ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#contains(java.lang.String)
	 */

	public boolean contains(String key) {
		return this.jedisCluster.exists(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hset(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */

	public boolean hset(String key, String field, String value) {
		long n = this.jedisCluster.hset(key, field, value);
		if (n == 1L || n == 0L) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hget(java.lang.String,
	 * java.lang.String)
	 */

	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hexists(java.lang.String,
	 * java.lang.String)
	 */

	public boolean hexists(String key, String field) {
		return this.jedisCluster.hexists(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hdel(java.lang.String,
	 * java.lang.String)
	 */

	public long hdel(String key, String field) {
		return jedisCluster.hdel(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hlen(java.lang.String)
	 */

	public long hlen(String key) {
		return jedisCluster.hlen(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hkeys(java.lang.String)
	 */

	public Set<String> hkeys(String key) {
		return jedisCluster.hkeys(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hvals(java.lang.String)
	 */

	public List<String> hvals(String key) {
		return jedisCluster.hvals(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hgetAll(java.lang.String)
	 */

	public Map<String, String> hgetAll(String key) {
		return jedisCluster.hgetAll(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hset(byte[], byte[], byte[])
	 */

	public boolean hset(byte[] key, byte[] field, byte[] value) {
		long n = jedisCluster.hset(key, field, value);
		return n == 1L || n == 0L ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hget(byte[], byte[])
	 */

	public byte[] hget(byte[] key, byte[] field) {
		return jedisCluster.hget(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hexists(byte[], byte[])
	 */

	public boolean hexists(byte[] key, byte[] field) {
		return jedisCluster.hexists(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hdel(byte[], byte[])
	 */

	public long hdel(byte[] key, byte[] field) {
		return jedisCluster.hdel(key, field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hlen(byte[])
	 */

	public long hlen(byte[] key) {
		return jedisCluster.hlen(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hkeys(byte[])
	 */

	public Set<byte[]> hkeys(byte[] key) {
		return jedisCluster.hkeys(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hvals(byte[])
	 */

	public List<byte[]> hvals(byte[] key) {
		return (List<byte[]>) jedisCluster.hvals(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#hgetAll(byte[])
	 */

	public Map<byte[], byte[]> hgetAll(byte[] key) {
		return jedisCluster.hgetAll(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#remove(byte[])
	 */

	public boolean remove(byte[] key) {
		return this.jedisCluster.del(key) == 1L ? true : false;
	}

	public Long ttl(String key) {
		return this.jedisCluster.ttl(key);
	}

	public Long ttl(byte[] key) {
		return this.jedisCluster.ttl(key);
	}

	public void expire(String key, int seconds) {
		this.jedisCluster.expire(key, seconds);
	}

	public void expire(byte[] key, int seconds) {
		this.jedisCluster.expire(key, seconds);
	}

	public void close() {
		try {
			if (jedisCluster != null) {
				logger.debug("开始关闭缓存");
				jedisCluster.close();
				logger.debug("关闭缓存完成");
			}
		} catch (Exception e) {
			logger.error("缓存连接关闭失败");
		}
	}


	public Integer getDataExpireTime() {
		return dataExpireTime;
	}

	public void setDataExpireTime(Integer dataExpireTime) {
		this.dataExpireTime = dataExpireTime;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public boolean clearAll() {
		return false;
	}

	public boolean isExpired(String key) {
		if(contains(key)) {
			return this.jedisCluster.ttl(key) > 0; 
		}
		return false;
	}

	public Set<String> getAllKeys() {
		return null;
	}

}
