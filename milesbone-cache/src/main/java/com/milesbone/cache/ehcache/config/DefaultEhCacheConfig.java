package com.milesbone.cache.ehcache.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.ehcache.IEHcacheConfig;
import com.milesbone.util.PropertyFileUtil;


/**
 * 
 * @Title  DefaultEhCacheConfig.java
 * @Package com.milesbone.cache.ehcache.config
 * @Description    默认EHCache配置类
 * @author miles
 * @date   2018-09-27 11:01
 */
public class DefaultEhCacheConfig implements IEHcacheConfig{
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultEhCacheConfig.class);
	
	private static final String EHCACHE_CONFIG_PATH = "/ehcache.properties";
	
	/**
	 * 默认缓存名称
	 */
	public static final String EHCACHE_DEFAULT_CACHE_NAME = "echache.default.cacheName";
	
	/**
	 * 默认失效时间
	 */
	public static final String EHCACHE_DEFAULT_EXPIRE_TIME = "ehcache.default.expreTime";
	
	
	private Properties ehcacheProperties;
	
	private DefaultEhCacheConfig(){
		init();
	}
	
	public static DefaultEhCacheConfig getInstance(){
		return DefaultEhCacheConfigurationHolder.instance;
	}
	
	private static class DefaultEhCacheConfigurationHolder{
		private final static DefaultEhCacheConfig instance = new DefaultEhCacheConfig();
	}
	
	
	private void init() {
		logger.debug("初始化ehcache配置文件");
		// 加载ehcache配置文件
		if (ehcacheProperties == null) {
			ehcacheProperties = PropertyFileUtil.load(EHCACHE_CONFIG_PATH);
		}
		logger.debug("初始化ehcache配置文件完成");
	}


	
	
	public Properties getEhcacheProperties() {
		return ehcacheProperties;
	}


	public void setEhcacheProperties(Properties ehcacheProperties) {
		this.ehcacheProperties = ehcacheProperties;
	}


	
	/* (non-Javadoc)
	 * @see com.milesbone.cache.ehcache.IEHcacheConfig#getDefaultCacheName()
	 */
	public String getDefaultCacheName() {
		return ehcacheProperties.getProperty(EHCACHE_DEFAULT_CACHE_NAME, "defaultCache");
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.ehcache.IEHcacheConfig#getDefaultExpireTime()
	 */
	public int getDefaultExpireTime() {
		return Integer.parseInt(ehcacheProperties.getProperty(EHCACHE_DEFAULT_EXPIRE_TIME, "86400"));
	}
	
	

	public Properties getProp() {
		return ehcacheProperties;
	}

}
