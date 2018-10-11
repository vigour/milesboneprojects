package com.milesbone.cache.ehcache;

import com.milesbone.common.config.IConfiguration;


/**
 * 
 * @Title  IEHcacheConfig.java
 * @Package com.milesbone.cache.ehcache
 * @Description    EHCache配置类
 * @author miles
 * @date   2018-09-27 10:59
 */
public interface IEHcacheConfig extends IConfiguration {


	/**
	 * 获取默认缓存名称
	 * @return
	 */
	public String getDefaultCacheName();

	
	/**
	 * 获取默认的失效时间 单位秒 默认 1天
	 * @return
	 */
	public int getDefaultExpireTime();

}
