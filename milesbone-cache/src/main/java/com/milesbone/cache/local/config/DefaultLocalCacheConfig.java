package com.milesbone.cache.local.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.local.ILocalCacheConfig;
import com.milesbone.util.PropertyFileUtil;

/**
 * 本地默认缓存配置类
 * 
 * @Title DefaultLocalCacheConfig.java
 * @Package com.milesbone.cache.local.config
 * @Description TODO
 * @author miles
 * @date 2018-10-04 15:42
 */
public class DefaultLocalCacheConfig implements ILocalCacheConfig {

	private static final Logger logger = LoggerFactory.getLogger(DefaultLocalCacheConfig.class);

	private static final String LOCAL_CACHE_CONFIG_PATH = "/localcache.properties";

	/**
	 * 默认缓存容量
	 */
	public static final String DEFAULT_CAPACITY = "local.defaultCapacity";

	/**
	 * 最大容量
	 */
	public static final String MAX_CAPACITY = "local.maxCapacity";

	/**
	 * 刷新缓存频率 (秒)
	 */
	public static final String MONITOR_DURATION = "local.monitorDuration";

	/**
	 * 默认有效时间 (秒)
	 */
	public static final String DEFAULT_EXPIRE = "local.defaultExpire";

	private Properties localCacheProp;

	private DefaultLocalCacheConfig() {
		init();
	}

	private void init() {
		logger.debug("初始化local cache配置文件");
		// 加载localcache配置文件
		if (localCacheProp == null) {
			localCacheProp = PropertyFileUtil.load(LOCAL_CACHE_CONFIG_PATH);
		}

		// 缓存监控线程启动

		logger.debug("初始化local cache配置文件完成");
	}

	public static DefaultLocalCacheConfig getInstance() {
		return DefaultLocalCacheConfigurationHolder.instance;
	}

	private static class DefaultLocalCacheConfigurationHolder {
		private final static DefaultLocalCacheConfig instance = new DefaultLocalCacheConfig();
	}

	public long getMaxCapacity() {
		return Long.parseLong(localCacheProp.getProperty(MAX_CAPACITY, "100000"));
	}

	public long getDefaultCapacity() {
		return Long.parseLong(localCacheProp.getProperty(DEFAULT_CAPACITY, "512"));
	}

	public long getDefaultExpireTime() {
		return Long.parseLong(localCacheProp.getProperty(DEFAULT_EXPIRE, "0"));
	}
	
	public Properties getProp() {
		return localCacheProp;
	}
}
