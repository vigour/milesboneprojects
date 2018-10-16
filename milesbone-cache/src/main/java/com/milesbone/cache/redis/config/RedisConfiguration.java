package com.milesbone.cache.redis.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.config.IConfiguration;
import com.milesbone.util.PropertyFileUtil;

/**
 * Redis配置类
 * @author miles
 * @date 2017-03-25 下午9:07:40
 */
public class RedisConfiguration implements IConfiguration{

	private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);
	
	private Properties redisProperties;
	
	private static final String REDIS_CONFIG_PATH = "/redis.properties";
	
	/**
	 * 定义连接字符串的key
	 */
//	public static final String REDIS_SERVERS_CONFIG = "redis.servers"; 
	public static final String REDIS_SERVERS_CONFIG = "spring.redis.cluster.nodes"; 
	
	
	/**
	 * 定义集群最多重定向次数(默认5)，
	 */
	public static final String REDIS_DEFAULT_MAX_REDIRECTIONS = "spring.redis.cluster.max-redirects";
	

	/**
	 * 定义redis 集群集合超时时间 默认2s
	 */
	public static final String REDIS_DEFAULT_CONNECTION_TIMEOUT = "redis.default.connection.timeout";
	
	/**
	 * 定义默认数据有效期key
	 * 设置默认数据有效期 ，默认30天（2592000秒）
	 */
	public static final String REDIS_DEFAULT_TIMEOUT_CONFIG = "redis.default.timeout";
	
	
	/**
	 *  控制一个pool最少有多少个状态为idle(空闲的)的jedis实例
	 */
	public static final String REDIS_POOL_MIN_IDEL_CONFIG = "redis.pool.min.idle";
	
	/**
	 * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例
	 */
	public static final String REDIS_POOL_MAX_IDEL_CONFIG = "redis.pool.max.idle";
	
	/**
	 * poll最多的总数
	 */
	public static final String REDIS_POOL_MAX_TOTAL_CONFIG = "redis.pool.max.total";
	
	/**
	 * pool操作最大等待时间
	 */
	public static final String REDIS_POOL_MAX_TIMEOUT_CONFIG = "redis.pool.max.timeout";
	
	
	public static final String REDIS_POOL_TEST_ON_BORROW = "redis.pool.testOnBorrow";
	
	/**
	 * redis 默认锁的key
	 */
	public static final String REDIS_DEFAULT_LOCK_KEY = "redis.default.lock.key";
	
	/**
	 * redis 默认锁失效时间
	 */
	public static final String REDIS_DEFAULT_LOCK_EXPIRE_TIME = "redis.default.lock.expireTime";
	
	/**
	 * @param redisProperties
	 */
	private RedisConfiguration() {
		init();
	}


	public static RedisConfiguration getInstance(){
		return RedisConfigurationHolder.instance;
	}
	
	private static class RedisConfigurationHolder{
		private final static RedisConfiguration instance = new RedisConfiguration();
	}
	
	/**
	 * 初始化redis配置文件
	 */
	private void init() {
		logger.debug("初始化redis配置文件");
		// 加载redis配置文件
		if (redisProperties == null) {
			redisProperties = PropertyFileUtil.load(REDIS_CONFIG_PATH);
		}
		logger.debug("初始化redis配置文件完成");
	}


	public Properties getRedisProperties() {
		return redisProperties;
	}
	
	public Properties getProp() {
		return redisProperties;
	}
	
	
}
