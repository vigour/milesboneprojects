package com.milesbone.redis.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.redis.config.RedisConfiguration;
import com.milesbone.redis.service.IRedisCache;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * redis集群实现
 * @author miles
 * @date 2017-03-26 下午11:51:33
 */
public class RedisClusterImpl implements IRedisCache{

	private static final Logger logger = LoggerFactory.getLogger(RedisClusterImpl.class);
	
	/**
	 * jedis集群操作对象
	 */
	private JedisCluster jedisCluster;
	
	private Properties redisProp;//redis配置文件
	
	private String nodeList;//redis连接串
	
	private int defaultTimeout;//设置默认数据有效期 ，默认30天（2592000秒）
	
	private ReentrantLock lock = new ReentrantLock();
	/**
	 *  
	 * 
	 */
	public RedisClusterImpl()  {
		init();
	}



	/**
	 * redis集群初始化
	 *  
	 */
	private void init() {
		logger.debug("redis 集群初始化开始...");
		if(redisProp == null){
			this.redisProp = RedisConfiguration.getInstance().getRedisProperties();
		}
		if(nodeList == null){
			this.nodeList = this.redisProp.getProperty(RedisConfiguration.REDIS_SERVERS_CONFIG);
		}
		if(defaultTimeout == 0){
			this.defaultTimeout = Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_TIMEOUT_CONFIG,"2592000"));
		}
		Set<HostAndPort> nodesList = this.parseHostAndPort(nodeList);
		
		lock.lock();
		
		GenericObjectPoolConfig poolConfig= new GenericObjectPoolConfig();
		poolConfig.setMinIdle(Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MIN_IDEL_CONFIG, "50")));
		poolConfig.setMaxIdle(Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_IDEL_CONFIG, "100")));
		poolConfig.setMaxTotal(Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_TOTAL_CONFIG, "1000")));
		poolConfig.setMaxWaitMillis(Long.parseLong(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_TIMEOUT_CONFIG, "180000")));
		
		this.jedisCluster = new JedisCluster(nodesList, poolConfig);
		
		lock.unlock();
		logger.debug("redis 集群初始化完成");
	}




	/**
	 * 解析redis服务器连接串
	 * @param redisNodes
	 * @return
	 * @throws Exception 
	 */
	private Set<HostAndPort> parseHostAndPort(String redisNodes) {
		if(StringUtils.isBlank(redisNodes)){
			logger.error("参数redisNodes不能为空");
			throw new IllegalArgumentException("参数redisNodes不能为空");
		}
		Set<HostAndPort> nodesList = new HashSet<>();
		try {
			String[] redisnode = redisNodes.split(",");
			for (int i = 0; i < redisnode.length; i++) {
				String[] split = redisnode[i].split(":");
				HostAndPort hostPort = new HostAndPort(split[0], Integer.parseInt(split[1]));
				nodesList.add(hostPort);
			} 
		} catch (Exception e) {
			logger.error("字符串解析失败,请检查参数格式:{}",redisNodes);
			e.printStackTrace();
		}
		return nodesList;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#save(java.lang.String, java.lang.String)
	 */
	
	public boolean save(String key, String value) {
		if(!this.jedisCluster.exists(key)){
			return this.jedisCluster.setex(key, defaultTimeout, value).equals("OK") ? true : false;
		}
		return false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#save(java.lang.String, java.lang.String, long)
	 */
	
	public boolean save(String key, String value, long time) {
		if(!this.jedisCluster.exists(key)){
			return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#saveOrUpdate(java.lang.String, java.lang.String)
	 */
	
	public boolean saveOrUpdate(String key, String value) {
		return this.jedisCluster.setex(key, defaultTimeout, value).equals("OK") ? true : false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#saveOrUpdate(java.lang.String, java.lang.String, long)
	 */
	
	public boolean saveOrUpdate(String key, String value, long time) {
		return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#update(java.lang.String, java.lang.String)
	 */
	
	public boolean update(String key, String value) {
		if(!this.jedisCluster.exists(key)){
			return this.jedisCluster.set(key, value).equals("OK") ? true : false;
		}
		return false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#update(java.lang.String, java.lang.String, long)
	 */
	
	public boolean update(String key, String value, long time) {
		if(!this.jedisCluster.exists(key)){
			return this.jedisCluster.setex(key, new Long(time).intValue(), value).equals("OK") ? true : false;
		}
		return false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#get(java.lang.String)
	 */
	
	public String get(String key) {
		return this.jedisCluster.get(key);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#remove(java.lang.String)
	 */
	
	public boolean remove(String key) {
		return this.jedisCluster.del(key) == 1L ? true : false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#exist(java.lang.String)
	 */
	
	public boolean exist(String key) {
		return this.jedisCluster.exists(key);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hset(java.lang.String, java.lang.String, java.lang.String)
	 */
	
	public boolean hset(String key, String field, String value) {
		long n = this.jedisCluster.hset(key, field, value);
		if(n == 1L || n == 0L){
			return true;
		}
		return false;
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hget(java.lang.String, java.lang.String)
	 */
	
	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hexists(java.lang.String, java.lang.String)
	 */
	
	public boolean hexists(String key, String field) {
		return this.jedisCluster.hexists(key, field);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hdel(java.lang.String, java.lang.String)
	 */
	
	public long hdel(String key, String field) {
		return jedisCluster.hdel(key, field);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hlen(java.lang.String)
	 */
	
	public long hlen(String key) {
		return jedisCluster.hlen(key);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hkeys(java.lang.String)
	 */
	
	public Set<String> hkeys(String key) {
		return jedisCluster.hkeys(key);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hvals(java.lang.String)
	 */
	
	public List<String> hvals(String key) {
		return jedisCluster.hvals(key);
	}




	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hgetAll(java.lang.String)
	 */
	
	public Map<String, String> hgetAll(String key) {
		return jedisCluster.hgetAll(key);
	}
	
	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hset(byte[], byte[], byte[])
	 */
	
	public boolean hset(byte[] key,byte[] field,byte[] value) {
		long n = jedisCluster.hset(key, field, value);
		return n==1L||n==0L ? true :false ;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hget(byte[], byte[])
	 */
	
	public byte[] hget(byte[] key, byte[] field) {
		return jedisCluster.hget(key, field);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hexists(byte[], byte[])
	 */
	
	public boolean hexists(byte[] key, byte[] field) {
		return jedisCluster.hexists(key, field);
	}
	

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hdel(byte[], byte[])
	 */
	
	public long hdel(byte[] key, byte[] field) {
		return jedisCluster.hdel(key, field);
	}
	

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hlen(byte[])
	 */
	
	public long hlen(byte[] key) {
		return jedisCluster.hlen(key);
	}
	

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hkeys(byte[])
	 */
	
	public Set<byte[]> hkeys(byte[] key) {
		return jedisCluster.hkeys(key);
	}
	

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hvals(byte[])
	 */
	
	public List<byte[]> hvals(byte[] key) {
		return (List<byte[]>) jedisCluster.hvals(key);
	}
	

	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#hgetAll(byte[])
	 */
	
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		return jedisCluster.hgetAll(key);
	}
	
	/* (non-Javadoc)
	 * @see com.milesbone.common.cache.ICache#remove(byte[])
	 */
	
	public boolean remove(byte[] key) {
		return this.jedisCluster.del(key) == 1L ? true : false;
	}
}