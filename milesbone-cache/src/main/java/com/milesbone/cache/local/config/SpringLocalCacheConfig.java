package com.milesbone.cache.local.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.milesbone.cache.local.ILocalCacheConfig;


/**
 * Spring 本地缓存资源加载配置
 * @Title  SpringLocalCacheConfig.java
 * @Package com.milesbone.cache.local.config
 * @Description    TODO
 * @author miles
 * @date   2018-10-08 15:54
 */
public class SpringLocalCacheConfig implements ILocalCacheConfig{
	
	private static final Logger logger = LoggerFactory.getLogger(SpringLocalCacheConfig.class);
	
	
	private Resource localConfig;
	
	private Long defaultCapacity;
	
	private Long defaultExpireTime;
	
	private Long maxCapacity;

	public SpringLocalCacheConfig() {
		this(null);
	}

	
	
	public SpringLocalCacheConfig(Resource localConfig) {
		this(localConfig, null);
	}



	public SpringLocalCacheConfig(Resource localConfig, Long defaultCapacity) {
		this(localConfig, defaultCapacity, null);
	}



	public SpringLocalCacheConfig(Resource localConfig, Long defaultCapacity, Long defaultExpireTime) {
		this(localConfig, defaultCapacity, defaultExpireTime,null);
	}



	public SpringLocalCacheConfig(Resource localConfig, Long defaultCapacity, Long defaultExpireTime,
			Long maxCapacity) {
		super();
		try {
			logger.debug("SpringLocalCacheConfig 构造方法初始化...");
			Properties localPorp = new Properties();
			if(localConfig == null) {
				localPorp = DefaultLocalCacheConfig.getInstance().getProp();
			}else {
				localPorp.load(localConfig.getInputStream());
			}
			this.localConfig = localConfig;
			
			if(defaultCapacity == null) {
				defaultCapacity = Long.parseLong(localPorp.getProperty(DefaultLocalCacheConfig.DEFAULT_CAPACITY, "512"));
			}
			this.defaultCapacity = defaultCapacity;
			
			if(defaultExpireTime == null) {
				defaultExpireTime = Long.parseLong(localPorp.getProperty(DefaultLocalCacheConfig.DEFAULT_EXPIRE, "0"));
			}
			this.defaultExpireTime = defaultExpireTime;
			
			if(maxCapacity == null) {
				maxCapacity =  Long.parseLong(localPorp.getProperty(DefaultLocalCacheConfig.MAX_CAPACITY, "100000"));
			}
			this.maxCapacity = maxCapacity;
			
			logger.debug("SpringLocalCacheConfig 构造方法初始化完成");
		} catch (IOException e) {
			logger.error("初始化SpringLocalCacheConfig 异常:{}", e.getMessage());
			e.printStackTrace();
		}
	}



	public static Logger getLogger() {
		return logger;
	}



	public void setDefaultCapacity(Long defaultCapacity) {
		this.defaultCapacity = defaultCapacity;
	}



	public void setDefaultExpireTime(Long defaultExpireTime) {
		this.defaultExpireTime = defaultExpireTime;
	}



	public Properties getProp() {
		Properties localPorp = new Properties();
		if(localConfig == null) {
			localPorp = DefaultLocalCacheConfig.getInstance().getProp();
		}else {
			try {
				localPorp.load(localConfig.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return localPorp;
	}

	public long getDefaultCapacity() {
		return defaultCapacity;
	}

	public long getDefaultExpireTime() {
		return defaultExpireTime;
	}

	public long getMaxCapacity() {
		return maxCapacity;
	}

	public Resource getLocalConfig() {
		return localConfig;
	}

	public void setLocalConfig(Resource localConfig) {
		this.localConfig = localConfig;
	}

}
