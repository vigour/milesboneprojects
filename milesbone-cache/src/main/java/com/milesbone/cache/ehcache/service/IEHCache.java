package com.milesbone.cache.ehcache.service;


import net.sf.ehcache.Cache;
import com.milesbone.common.cache.ICache;



/**
 * 
 * @Title  IEHCache.java
 * @Package com.milesbone.cache.ehcache.service
 * @Description    继承自ICache接口和Spring自带cache
 * @author miles
 * @date   2018-09-26 17:55
 */
public interface IEHCache extends ICache<String>,org.springframework.cache.Cache{

	/**
	 * 根据cache名称获取EhCache对象
	 * @param cache
	 * @return
	 */
	public Cache getEHCache(String  cacheName);
	
	
	/**
	 * 如果不存在则创建EHCache对象
	 * @param cacheName
	 * @return
	 */
	public Cache getEHCacheIfAbsent(String cacheName);
	
	/**
	 * 设置缓存
	 * @param cacheName
	 * @param cacheKey
	 * @param value
	 */
	public boolean putCache(String cacheName, String cacheKey, Object value);
	
	
	/**
     * 设置缓存
     *
     * @param cacheName         缓存名称
     * @param cacheKey          缓存key
     * @param value             值
     * @param timeToLiveSeconds 存在时间，单位秒
     */
    public boolean putCache(String cacheName, String cacheKey, Object value, int timeToLiveSeconds);
    
    
    
    /**
     * 获取缓存
     * @param cacheName
     * @param cacheKey
     * @return
     */
    public Object getCacheValue(String cacheName, String cacheKey);
    
    
    
    /**
     * 清除缓存
     * @param cacheName
     * @param cacheKey
     */
    public void clearCache(String cacheName, String cacheKey);
    
    
    
    /**
     * 判断缓存key是否存在
     * @param cachename
     * @param key
     * @return
     */
    public boolean contains(String cacheName, String key);
    
    
    
    /**
     * 判断是否失效
     * @param cacheName
     * @param key
     * @return
     */
    public boolean isExpired(String cacheName, String key);
    

}
