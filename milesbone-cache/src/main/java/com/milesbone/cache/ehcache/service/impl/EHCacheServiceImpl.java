package com.milesbone.cache.ehcache.service.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;

import com.milesbone.cache.ehcache.IEHcacheConfig;
import com.milesbone.cache.ehcache.config.DefaultEhCacheConfig;
import com.milesbone.cache.ehcache.service.IEHCache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 
 * @Title  EHCacheServiceImpl.java
 * @Package com.milesbone.cache.ehcache.service.impl
 * @Description    EHCache 缓存实现类
 * @author miles
 * @date   2018-09-27 15:20
 */
public class EHCacheServiceImpl implements IEHCache{
	
	private static final Logger logger = LoggerFactory.getLogger(EHCacheServiceImpl.class);
	
	private IEHcacheConfig config;
	
	private CacheManager cacheManager;

	private static byte[] lock = new byte[0];
	
	private Cache cache;
	
	private String name;
	
	public EHCacheServiceImpl() {
		super();
	}

	
	public Cache getEHCache() {
		if(cache == null) {
			logger.error("默认cache为空,创建新缓存");
			return getEHCache(DefaultEhCacheConfig.getInstance().getDefaultCacheName());
		}
		return cache;
	}

	public Cache getEHCache(String cacheName) {
		return getEHCacheIfAbsent(cacheName);
	}

	
	public Cache getEHCacheIfAbsent(String cacheName) {
		if(cacheManager == null) {
			logger.error("Ehcache缓存值为空需要创建新缓存");
			throw new IllegalArgumentException("Need a cacheManager. Please  create it.");
		}
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			logger.info("cache 不存在,创建新的缓存");
	        synchronized (lock) {
	            cache = cacheManager.getCache(cacheName);
	            if (cache == null) {
	            	cacheManager.addCacheIfAbsent(cacheName);
	                cache = cacheManager.getCache(cacheName);
	            }
	        }
	        logger.info("cache:缓存{}创建完成",cacheName);
	        if (cache == null) {
                logger.error("EHCache: cache not config and not auto created, cacheName:{}",cacheName);
            }
		}
		return cache;
	}

	public boolean putCache(String key, String value) {
		return putCache(DefaultEhCacheConfig.getInstance().getDefaultCacheName(), key, value);
	}


	public boolean putCache(String key, String value, long time) {
		if(time > Integer.MAX_VALUE) {
			logger.error("参数expireTime值:{}超过最大整数值,无法将long转换为int", time);
			throw new IllegalArgumentException("time参数不合法");
		}
		return putCache(DefaultEhCacheConfig.getInstance().getDefaultCacheName(), key, value, (int)time);
	}


	public boolean putOrReplace(String key, String value) {
		return putCache(key, value);
	}


	public boolean putOrReplace(String key, String value, long time) {
		return putCache(key, value, time);
	}


	public boolean replace(String key, String value) {
		if(!contains(key)) {
			return putCache(key, value);
		}
		return false;
	}


	public boolean replace(String key, String value, long time) {
		if(!contains(key)) {
			return putCache(key, value, (int)time);
		}
		return false;
	}


	public String getCache(String key) {
        if(contains(key)) {
        	Element element = cache.get(key);
            if (element == null || element.isExpired()) {
                return null;
            }
            return String.valueOf(element.getObjectValue());
        }
        return null;
	}


	public Object getCacheValue(String cacheName, String cacheKey) {
		Cache cache = getEHCache(cacheName);
        if (cache == null) {
        	logger.error("{}缓存不存在,无法获取缓存key{}对应值,请确认参数是否正确", cacheName);
            return null;
        }
        if(contains(cacheName, cacheKey)) {
        	Element element = cache.get(cacheKey);
            if (element == null || element.isExpired()) {
                return null;
            }
            return element.getObjectValue();
        }
        return null;
	}
	
	public boolean remove(String key) {
		if(cache == null) {
			cache = this.getEHCache();
		}
		if(contains(key)) {
			cache.remove(key);
		}
		return false;
	}


	public boolean clearAll() {
		try {
			if(cache == null) {
				cache = this.getCache();
			}
			cache.removeAll();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		}
		return true;
	}


	public boolean contains(String key) {
		if(cache == null) {
			cache = this.getEHCache();
		}
		return (cache.isKeyInCache(key) && cache.getQuiet(key) != null);
	}
	
	
	public boolean contains(String cacheName, String key) {
		Cache cache = getEHCache(cacheName);
		if(cache == null) {
			logger.error("缓存{}不存在,确认参数是否正确", cacheName);
			return false;
		}
		return (cache.isKeyInCache(key) && cache.getQuiet(key) != null);
	}


	public boolean isExpired(String key) {
		if(contains(key)) {
			Element element = cache.get(key);
			return cache.isExpired(element);
		}
		return false;
	}
	
	
	public boolean isExpired(String cacheName, String key) {
		Cache cache = getEHCache(cacheName);
		if(cache == null) {
			logger.error("缓存{}不存在,确认参数是否正确", cacheName);
			return false;
		}
		if(contains(cacheName, key)) {
			Element element = cache.get(key);
			return cache.isExpired(element);
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public Set<String> getAllKeys() {
		if(cache == null) {
			cache = this.getEHCache();
		}
		return new LinkedHashSet<String>(cache.getKeysWithExpiryCheck());
	}


	public boolean putCache(String cacheName, String cacheKey, Object value) {
		return putCache(cacheName, cacheKey, value, DefaultEhCacheConfig.getInstance().getDefaultExpireTime());
	}


	public boolean putCache(String cacheName, String cacheKey, Object value, int timeToLiveSeconds) {
		try {
			Cache cache = getEHCache(cacheName);
			if(cache == null) {
				logger.error("EHCache: cache not config and not auto created, cacheName:{}",cacheName);
				return false;
			}
			cache.put(new Element(cacheKey, value, timeToLiveSeconds, timeToLiveSeconds));
			logger.debug("cache:{},缓存key:{}成功",cacheName, cacheKey);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		}
		return false;
	}


	


	public void clearCache(String cacheName, String cacheKey) {
		Cache cache = getEHCache(cacheName);
		if(cache == null) {
			return ;
		}
		cache.remove(cacheKey);
	}


	public CacheManager getCacheManager() {
		return cacheManager;
	}


	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	

	public Cache getCache() {
		return getEHCache();
	}


	public void setCache(Cache cache) {
		this.cache = cache;
	}


	public IEHcacheConfig getConfig() {
		return config;
	}


	public void setConfig(IEHcacheConfig config) {
		this.config = config;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public Object getNativeCache() {
		return this;
	}


	public ValueWrapper get(Object key) {
		Element value = cache.get(key);
		logger.debug("Cache ehcache :{}{}={}", name,key,value);
		if (value != null) {
			// TODO 访问10次EhCache 强制访问一次redis 使得数据不失效//
//			if (value.getHitCount() < activeCount) {
				return (value != null ? new SimpleValueWrapper(value.getObjectValue()) : null);
//			} else {
//				value.resetAccessStatistics();
//			}
				
		}
		return null;
	}



	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if (StringUtils.isBlank(String.valueOf(key)) || null == type) {
			return null;
		} else {
			// final String finalKey;
			final Class<T> finalType = type;
			// if (key instanceof String) {
			// finalKey = (String) key;
			// } else {
			// finalKey = key.toString();
			// }
			// final Object object = this.get(finalKey);
			final Object object = this.get(key);
			if (finalType != null && finalType.isInstance(object) && null != object) {
				return (T) object;
			} else {
				return null;
			}
		}
	}

	public void put(Object key, Object value) {
		cache.put(new Element(key, value));
	}


	public ValueWrapper putIfAbsent(Object key, Object value) {
		// final String finalKey;
		if (StringUtils.isBlank(String.valueOf(key)) || StringUtils.isBlank(String.valueOf(value))) {
			return null;
		} else {
			// if (key instanceof String) {
			// finalKey = (String) key;
			// } else {
			// finalKey = key.toString();
			// }
			// if (!StringUtils.isEmpty(finalKey)) {
			// final Object finalValue = value;
			this.put(key, value);
			// }
		}
		
		return new SimpleValueWrapper(value);
	}


	public void evict(Object key) {
		cache.remove(key);
	}


	public void clear() {
		cache.removeAll();
	}

}
