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
public class RedisClusterImpl implements IRedisCache {

	private static final Logger logger = LoggerFactory.getLogger(RedisClusterImpl.class);

	/**
	 * jedis集群操作对象
	 */
	private JedisCluster jedisCluster;

	private IRedisConfig redisConfig;

	private ReentrantLock lock = new ReentrantLock();

	/**
	 *  
	 * 
	 */
	public RedisClusterImpl() {
		this(null);
	}

	/**
	 * @param redisConfig
	 */
	public RedisClusterImpl(IRedisConfig redisConfig) {
		this(redisConfig, null);
	}

	/**
	 * @param jedisCluster
	 */
	public RedisClusterImpl(IRedisConfig redisConfig, JedisCluster jedisCluster) {
		super();
		logger.debug("redis 构造方法开始");
		if (redisConfig == null) {
			redisConfig = new DefaultRedisConfig();
		}
		this.redisConfig = redisConfig;

		lock.lock();
		if (jedisCluster == null) {
			logger.debug("redis 集群初始化开始...");
			jedisCluster = new JedisCluster(redisConfig.getNodes(), redisConfig.getConnectionTimeout(),
					redisConfig.getMaxAttempts(), redisConfig.getPoolconfig());
			logger.debug("redis 集群初始化完成");
		}
		lock.unlock();
		
		this.jedisCluster = jedisCluster;
		logger.debug("redis 构造方法完成...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#save(java.lang.String,
	 * java.lang.String)
	 */
	public boolean save(String key, String value) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, redisConfig.getDataExpireTime(), value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#save(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean save(String key, String value, long time) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#saveOrUpdate(java.lang.String,
	 * java.lang.String)
	 */

	public boolean saveOrUpdate(String key, String value) {
		return this.jedisCluster.setex(key, redisConfig.getDataExpireTime(), value).equals("OK") ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#saveOrUpdate(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean saveOrUpdate(String key, String value, long time) {
		return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#update(java.lang.String,
	 * java.lang.String)
	 */

	public boolean update(String key, String value) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.set(key, value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#update(java.lang.String,
	 * java.lang.String, long)
	 */

	public boolean update(String key, String value, long time) {
		if (!this.jedisCluster.exists(key)) {
			return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.common.cache.ICache#get(java.lang.String)
	 */

	public String get(String key) {
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
	 * @see com.milesbone.common.cache.ICache#exist(java.lang.String)
	 */

	public boolean exist(String key) {
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

	
	
	public IRedisConfig getRedisConfig() {
		return redisConfig;
	}

	public void setRedisConfig(IRedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}


}
