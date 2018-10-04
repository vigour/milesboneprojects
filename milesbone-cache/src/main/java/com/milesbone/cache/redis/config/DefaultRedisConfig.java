package com.milesbone.cache.redis.config;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.redis.IRedisConfig;

import redis.clients.jedis.HostAndPort;

/**
 * 默认redis配置类,redisServers
 * 
 * @author miles
 * @date 2017-05-06 下午7:37:29
 */
public class DefaultRedisConfig implements IRedisConfig {

	private static final Logger logger = LoggerFactory.getLogger(DefaultRedisConfig.class);

	private String redisServers;

	private Set<HostAndPort> nodes;

	private Integer dataExpireTime;

	private Integer maxAttempts;

	private Integer connectionTimeout;

	private GenericObjectPoolConfig poolconfig;

	/**
	 * 
	 */
	public DefaultRedisConfig() {
		this(null);
	}

	/**
	 * @param redisServers
	 */
	public DefaultRedisConfig(String redisServers) {
		this(redisServers, 0);
	}
	
	

	/**
	 * @param redisServers
	 * @param dataExpireTime
	 */
	public DefaultRedisConfig(String redisServers, Integer dataExpireTime) {
		this(redisServers, dataExpireTime, 0);
	}
	
	
	
	
	/**
	 * @param redisServers
	 * @param dataExpireTime
	 * @param maxAttempts
	 */
	public DefaultRedisConfig(String redisServers, Integer dataExpireTime, Integer maxAttempts) {
		this(redisServers, dataExpireTime, maxAttempts, 0);
	}


	
	/**
	 * @param redisServers
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 */
	public DefaultRedisConfig(String redisServers, Integer dataExpireTime, Integer maxAttempts,
			Integer connectionTimeout) {
		this(redisServers, dataExpireTime, maxAttempts, connectionTimeout, null);
	}
	
	/**
	 * @param redisServers
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 * @param poolconfig
	 */
	public DefaultRedisConfig(String redisServers, Integer dataExpireTime, Integer maxAttempts,
			Integer connectionTimeout, GenericObjectPoolConfig poolconfig) {
		this(redisServers, null, dataExpireTime, maxAttempts, connectionTimeout, poolconfig);
	}

	/**
	 * @param nodes
	 * @param dataExpireTime
	 */
	public DefaultRedisConfig(Set<HostAndPort> nodes, Integer dataExpireTime) {
		this(nodes, dataExpireTime, 0);
	}

	/**
	 * @param nodes
	 * @param dataExpireTime
	 * @param maxAttempts
	 */
	public DefaultRedisConfig(Set<HostAndPort> nodes, Integer dataExpireTime, Integer maxAttempts) {
		this(nodes, dataExpireTime, maxAttempts, 0);
	}

	/**
	 * @param nodes
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 */
	public DefaultRedisConfig(Set<HostAndPort> nodes, Integer dataExpireTime, Integer maxAttempts,
			Integer connectionTimeout) {
		this(nodes, dataExpireTime, maxAttempts, connectionTimeout, null);
	}

	/**
	 * @param nodes
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 * @param poolconfig
	 */
	public DefaultRedisConfig(Set<HostAndPort> nodes, Integer dataExpireTime, Integer maxAttempts,
			int connectionTimeout, GenericObjectPoolConfig poolconfig) {
		this(null, nodes, dataExpireTime, maxAttempts, connectionTimeout, poolconfig);
	}


	
	/**
	 * @param redisServers
	 * @param nodes
	 * @param dataExpireTime
	 * @param maxAttempts
	 * @param connectionTimeout
	 * @param poolconfig
	 */
	public DefaultRedisConfig(String redisServers, Set<HostAndPort> nodes,
			Integer dataExpireTime, Integer maxAttempts, Integer connectionTimeout,
			GenericObjectPoolConfig poolconfig) {
		super();
		logger.debug("DefaultRedisConfig构造方法初始化...");
		try {
			Properties redisProp = RedisConfiguration.getInstance().getRedisProperties();

			if (StringUtils.isBlank(redisServers)) {
				redisServers = redisProp.getProperty(RedisConfiguration.REDIS_SERVERS_CONFIG, "127.0.0.1:6379");
			}
			this.redisServers = redisServers;
			
			if (nodes == null || nodes.size() == 0) {
				nodes = parseHostAndPort(redisServers);
			}
			this.nodes = nodes;
			
			if (this.nodes == null || this.nodes.size() == 0) {
				logger.error("redis节点不能为空");
				throw new IllegalArgumentException("参数错误:redis节点不能为空");
			}
			
			if (dataExpireTime == null || dataExpireTime == 0) {
				dataExpireTime = Integer
						.parseInt(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_TIMEOUT_CONFIG, "2592000"));
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
			
		} catch (Exception e) {
			logger.error("初始化redis默认配置异常: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("DefaultRedisConfig构造方法初始化完成");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getRedisServers()
	 */
	public String getRedisServers() {
		return redisServers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.milesbone.cache.redis.config.IRedisConfig#setRedisServers(java.lang.
	 * String)
	 */
	public void setRedisServers(String redisServers) {
		this.redisServers = redisServers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getDataExpireTime()
	 */
	public Integer getDataExpireTime() {
		return dataExpireTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#setDataExpireTime(int)
	 */
	public void setDataExpireTime(Integer dataExpireTime) {
		this.dataExpireTime = dataExpireTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getNodes()
	 */
	@Override
	public Set<HostAndPort> getNodes() {
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.milesbone.cache.redis.config.IRedisConfig#setNodes(java.util.Set)
	 */
	@Override
	public void setNodes(Set<HostAndPort> nodes) {
		this.nodes = nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getMaxAttempts()
	 */
	@Override
	public Integer getMaxAttempts() {
		return maxAttempts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#setMaxAttempts(int)
	 */
	@Override
	public void setMaxAttempts(Integer maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getConnectionTimeout()
	 */
	@Override
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.milesbone.cache.redis.config.IRedisConfig#setConnectionTimeout(int)
	 */
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.milesbone.cache.redis.config.IRedisConfig#getPoolconfig()
	 */
	@Override
	public GenericObjectPoolConfig getPoolconfig() {
		return poolconfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.milesbone.cache.redis.config.IRedisConfig#setPoolconfig(org.apache.
	 * commons.pool2.impl.GenericObjectPoolConfig)
	 */
	@Override
	public void setPoolconfig(GenericObjectPoolConfig poolconfig) {
		this.poolconfig = poolconfig;
	}

	/**
	 * 解析redis服务器连接串
	 * 
	 * @param redisNodes
	 * @return
	 * @throws Exception
	 */
	private static Set<HostAndPort> parseHostAndPort(String redisNodes) {
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

	@Override
	public Properties getProp() {
		return null;
	}
}
