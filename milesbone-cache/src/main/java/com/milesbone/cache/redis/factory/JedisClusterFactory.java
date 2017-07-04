package com.milesbone.cache.redis.factory;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.milesbone.cache.redis.IRedisConfig;
import com.milesbone.common.factory.IFactoryBean;

import redis.clients.jedis.JedisCluster;


/**
 * JedisCluster工厂实现
 * @author miles
 * @date 2017-05-14 下午5:18:24
 */
public class JedisClusterFactory implements IFactoryBean<JedisCluster>, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(JedisClusterFactory.class);
	
	private JedisCluster jedisCluster;
	
	private IRedisConfig redisConfig;

	

	/**
	 * 
	 */
	public JedisClusterFactory() {
		super();
	}

	public JedisCluster getObject() throws Exception {
		return this.jedisCluster;
	}

	public Class<? extends JedisCluster> getObjectType() {
		return (this.jedisCluster != null ? this.jedisCluster.getClass() : JedisCluster.class);
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		if(redisConfig == null){
			logger.error("redisConfig未配置无法获取jedisCluster");
			throw new IllegalArgumentException("redisConfig未配置无法获取jedisCluster");
		}
		
		if (jedisCluster == null) {
			ReentrantLock lock = new ReentrantLock();
			lock.lock();
			logger.debug("redis 集群初始化开始...");
			jedisCluster = new JedisCluster(redisConfig.getNodes(), redisConfig.getConnectionTimeout(),
					redisConfig.getMaxAttempts(), redisConfig.getPoolconfig());
			logger.debug("redis 集群初始化完成");
			lock.unlock();
		}
		
	}

	public IRedisConfig getRedisConfig() {
		return redisConfig;
	}

	public void setRedisConfig(IRedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}
	
}
