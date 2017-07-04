package com.milesbone.cache.redis.config;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.milesbone.cache.redis.IRedisConfig;

import redis.clients.jedis.HostAndPort;

/**
 * Spring 资源加载配置
 * 两种资源配置服务连接方式,区分方式用:addressKeyPrefix值为空判断,默认使用第一种
 * 第一种  redis.servers=127.0.0.1:6379,127.0.0.1:6380,127.0.0.1:6381
 * 第二种 addressKeyPrefix=xxx
 *      xxx1.redis.host.port=127.0.0.1:6379
 *      xxx2.redis.host.port=127.0.0.1:6380
 *      xxx3.redis.host.port=127.0.0.1:6381
 *      
 * @author miles
 * @date 2017-05-20 下午7:47:21
 */
public class SpringResourceRedisConfig implements IRedisConfig {

	private static final Logger logger = LoggerFactory.getLogger(SpringResourceRedisConfig.class);

	private Resource addressConfig;

	private String addressKeyPrefix;

	private Set<HostAndPort> nodes;
	
	private Integer dataExpireTime;

	private Integer maxAttempts;

	private Integer connectionTimeout;

	private GenericObjectPoolConfig poolconfig;

	/**
	 * 
	 */
	public SpringResourceRedisConfig() {
		this(null);
	}

	/**
	 * @param addressConfig
	 */
	public SpringResourceRedisConfig(Resource addressConfig) {
		this(addressConfig, null);
	}

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix) {
		this(addressConfig, addressKeyPrefix, null);
	}

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 * @param dataExpireTime
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix, Integer dataExpireTime) {
		this(addressConfig, addressKeyPrefix, dataExpireTime, null);
	}

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 * @param dataExpireTime
	 * @param maxAttempts
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix, Integer dataExpireTime,
			Integer maxAttempts) {
		this(addressConfig, addressKeyPrefix, dataExpireTime, maxAttempts, null);
	}

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix, Integer dataExpireTime,
			Integer maxAttempts, Integer connectionTimeout) {
		this(addressConfig, addressKeyPrefix, dataExpireTime, maxAttempts, connectionTimeout, null);
	}

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 * @param poolconfig
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix, Integer dataExpireTime,
			Integer maxAttempts, Integer connectionTimeout, GenericObjectPoolConfig poolconfig) {
		this(addressConfig, addressKeyPrefix, dataExpireTime, maxAttempts, connectionTimeout, poolconfig, null);
	}

	
	

	/**
	 * @param addressConfig
	 * @param addressKeyPrefix
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 * @param poolconfig
	 * @param nodes
	 */
	public SpringResourceRedisConfig(Resource addressConfig, String addressKeyPrefix, Integer dataExpireTime,
			Integer maxAttempts, Integer connectionTimeout, GenericObjectPoolConfig poolconfig,
			Set<HostAndPort> nodes) {
		super();
		logger.debug("SpringResourceRedisConfig构造方法初始化...");

		try {
			Properties redisProp = new Properties();
			if (addressConfig == null) {
				logger.debug("SpringResourceRedisConfig参数未配置,使用默认redis配置");
				redisProp = RedisConfiguration.getInstance().getRedisProperties();
			} else {
				redisProp.load(addressConfig.getInputStream());
			}
			this.addressConfig = addressConfig;

			if(StringUtils.isBlank(addressKeyPrefix)){
				logger.debug("addressKeyPrefix参数配置为空则使用默认的");
				nodes = parseHostAndPort(redisProp);
			}else{
				nodes = parseHostAndPort(redisProp, addressKeyPrefix);
			}
			this.nodes = nodes;
			
			if (this.nodes == null || this.nodes.size() == 0) {
				logger.error("redis节点不能为空");
				throw new IllegalArgumentException("参数错误:redis节点不能为空");
			}
			
			if (dataExpireTime == null || dataExpireTime == 0) {
				dataExpireTime = Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_TIMEOUT_CONFIG, "2592000"));
			}
			this.dataExpireTime = dataExpireTime;
			
			if (maxAttempts == null || maxAttempts == 0) {
				maxAttempts = Integer
						.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_MAX_REDIRECTIONS, "10"));
			}
			this.maxAttempts = maxAttempts;
			
			if (connectionTimeout == null || connectionTimeout == 0) {
				connectionTimeout = Integer
						.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_CONNECTION_TIMEOUT, "2000"));
			}
			this.connectionTimeout = connectionTimeout;
			
			if (poolconfig == null) {
				poolconfig = new GenericObjectPoolConfig();
				poolconfig.setMinIdle(
						Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MIN_IDEL_CONFIG, "50")));
				poolconfig.setMaxIdle(
						Integer.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_IDEL_CONFIG, "100")));
				poolconfig.setMaxTotal(Integer
						.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_TOTAL_CONFIG, "1000")));
				poolconfig.setMaxWaitMillis(Long
						.parseLong(redisProp.getProperty(RedisConfiguration.REDIS_POOL_MAX_TIMEOUT_CONFIG, "180000")));
			}
			this.poolconfig = poolconfig;
		} catch (IOException e) {
			logger.error("初始化redis配置异常: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("SpringResourceRedisConfig构造方法初始化完成");
	}


	/**
	 * 第二种配置解析
	 * @param redisProp
	 * @param addressKeyPrefix
	 * @return
	 */
	private Set<HostAndPort> parseHostAndPort(Properties redisProp, String addressKeyPrefix) {
		if(redisProp == null){
			logger.error("加载properties失败,redisProperties配置参数值不能为空");
			throw new IllegalArgumentException("加载properties失败,redisProperties配置参数值不能为空");
		}
		if(StringUtils.isBlank(addressKeyPrefix)){
			logger.error("addressKeyPrefix配置参数值不能为空");
			throw new IllegalArgumentException("addressKeyPrefix配置参数值不能为空");
		}
		
		Set<HostAndPort> nodesList = new HashSet<HostAndPort>();
		for (Object key : redisProp.keySet()) {
			if(! ((String) key).startsWith(addressKeyPrefix)){
				continue;
			}
			
			String value = redisProp.getProperty((String) key);
			if(IP_AND_PORT_PATTERN.matcher(value).matches()){
				String[] split = value.split(":");
				HostAndPort hostPort = new HostAndPort(split[0], Integer.parseInt(split[1]));
				nodesList.add(hostPort);
			}else{
				logger.error("解析redisNodes异常:{}, ip 或 port 不合法", value);
				throw new IllegalArgumentException("ip 或 port 不合法");
			}
			
		}
		
		return nodesList;
	}

	/**
	 * 解析配置默认配置文件
	 * @param redisProp
	 * @return
	 */
	private Set<HostAndPort> parseHostAndPort(Properties redisProp) {
		if(redisProp == null){
			logger.error("加载properties失败,redisProperties配置参数值不能为空");
			throw new IllegalArgumentException("加载properties失败,redisProperties配置参数值不能为空");
		}
		String redisNodes = redisProp.getProperty(RedisConfiguration.REDIS_SERVERS_CONFIG, "127.0.0.1:6379");
		if (StringUtils.isBlank(redisNodes)) {
			logger.error("参数redisNodes不能为空");
			throw new IllegalArgumentException("参数错误:redis节点不能为空");
		}
		
		Set<HostAndPort> nodesList = new HashSet<HostAndPort>();
		try {
			String[] redisnode = redisNodes.split(",");
			String value = null;
			for (int i = 0; i < redisnode.length; i++) {
				value = redisnode[i];
				if(IP_AND_PORT_PATTERN.matcher(value).matches()){
					String[] split = value.split(":");
					HostAndPort hostPort = new HostAndPort(split[0], Integer.parseInt(split[1]));
					nodesList.add(hostPort);
				}else{
					logger.error("解析redisNodes异常:{}, ip 或 port 不合法", value);
					throw new IllegalArgumentException("ip 或 port 不合法");
				}
			}
		} catch (Exception e) {
			logger.error("字符串解析失败,请检查参数格式:{}", redisNodes);
			e.printStackTrace();
		}
		return nodesList;
	}

	
	public Resource getAddressConfig() {
		return addressConfig;
	}

	public void setAddressConfig(Resource addressConfig) {
		this.addressConfig = addressConfig;
	}

	public String getAddressKeyPrefix() {
		return addressKeyPrefix;
	}

	public void setAddressKeyPrefix(String addressKeyPrefix) {
		this.addressKeyPrefix = addressKeyPrefix;
	}

	public Integer getDataExpireTime() {
		return dataExpireTime;
	}

	public void setDataExpireTime(Integer dataExpireTime) {
		this.dataExpireTime = dataExpireTime;
	}

	public Set<HostAndPort> getNodes() {
		return nodes;
	}

	public void setNodes(Set<HostAndPort> nodes) {
		this.nodes = nodes;
	}

	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public GenericObjectPoolConfig getPoolconfig() {
		return poolconfig;
	}

	public void setPoolconfig(GenericObjectPoolConfig poolconfig) {
		this.poolconfig = poolconfig;
	}

}
