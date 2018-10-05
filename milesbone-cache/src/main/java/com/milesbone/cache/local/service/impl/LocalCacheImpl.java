package com.milesbone.cache.local.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.common.entity.ICacheEntity;
import com.milesbone.cache.local.config.DefaultLocalCacheConfig;
import com.milesbone.cache.local.entity.LocalCacheEntity;
import com.milesbone.cache.local.service.ILocalCache;

/**
 * 本地缓存服务实现
 * @Title  LocalCacheImpl.java
 * @Package com.milesbone.cache.local.service
 * @Description    TODO
 * @author miles
 * @date   2018-10-05 10:00
 */
public class LocalCacheImpl implements ILocalCache{
	
	private static final Logger logger = LoggerFactory.getLogger(LocalCacheImpl.class);
	
	
	private static Map<String, ICacheEntity> cache = new ConcurrentHashMap<String, ICacheEntity>();

	
	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.service.impl.ILocalCache#putCache(java.lang.String, com.milesbone.cache.common.entity.ICacheEntity)
	 */
	public boolean putCache(String key, ICacheEntity entity) {
		try {
			if(StringUtils.isBlank(key)) {
				throw new IllegalArgumentException("LocalCache key is null");
			}else {
				cache.put(key, clone(entity));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * 将值通过序列化clone 处理后保存到缓存中，可以解决值引用的问题
	 * @param key
	 * @param value
	 * @param expireTime 过期时间
	 * @return
	 */
	private boolean putCacheValue(String key, Object value, long expireTime) {
		try {
			if(cache.size() > DefaultLocalCacheConfig.getInstance().getMaxCapacity()) {
				return false;
			}
			expireTime = expireTime > 0 ? expireTime : 0L;
			// 序列化赋值
			ICacheEntity entityClone = clone(new LocalCacheEntity(value, System.nanoTime(), expireTime));
			cache.put(key, entityClone);
		} catch (Exception e) {
			logger.error("{}方法异常.{}","putCacheValue()", e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 
	 * 序列化 克隆处理
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends Serializable> T clone(T object) {
		T cloneObject = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			T readObject = (T) ois.readObject();
			cloneObject = readObject;
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cloneObject;
	}

	public boolean putCache(String key, Object value) {
		try {
			if(StringUtils.isBlank(key)) {
				throw new IllegalArgumentException("LocalCache key is null");
			}else {
				return putCache(key, value, DefaultLocalCacheConfig.getInstance().getDefaultExpireTime());
			}
		} catch (NumberFormatException e) {
			logger.error("{}数字类型格式化异常.{}",DefaultLocalCacheConfig.DEFAULT_EXPIRE, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public boolean putCache(String key, Object value, long expireTime) {
		try {
			if(StringUtils.isBlank(key)) {
				throw new IllegalArgumentException("LocalCache key is null");
			}else {
				return putCacheValue(key, value, expireTime);
			}
		} catch (NumberFormatException e) {
			logger.error("{}数字类型格式化异常.{}",DefaultLocalCacheConfig.DEFAULT_EXPIRE, e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public boolean putOrReplace(String key, Object value) {
		return putCache(key, value);
	}

	public boolean putOrReplace(String key, Object value, long expireTime) {
		return putCache(key, value, expireTime);
	}

	public boolean replace(String key, Object value) {
		if(!contains(key)) {
			putCache(key, value);
		}
		return false;
	}

	public boolean replace(String key, Object value, long expireTime) {
		if(!contains(key)) {
			putCache(key, value, expireTime);
		}
		return false;
	}

	public Object getCache(String key) {
		if(this.contains(key)) {
			LocalCacheEntity entity = (LocalCacheEntity) cache.get(key);
			return entity.getValue();
		}
		return null;
	}
	
	
	public ICacheEntity getCacheEntity(String key) {
		if(this.contains(key)) {
			return (LocalCacheEntity) cache.get(key);
		}
		return null;
	}

	public boolean remove(String key) {
		if(this.contains(key)) {
			cache.remove(key);
			return true;
		}
		return false;
	}

	public boolean clearAll() {
		try {
			if(!cache.isEmpty()) {
				cache.clear();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean contains(String key) {
		return cache.containsKey(key);
	}

	public boolean isExpired(String key) {
		if(!this.contains(key)) {
			return true;
		}
		LocalCacheEntity entity = (LocalCacheEntity) cache.get(key);
		long timoutTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - entity.getGenerateTimestamp());

		if(timoutTime >= entity.getExpireTime()) {
			return true;
		}
		
		return false;
	}

	public Set<String> getAllKeys() {
		return cache.keySet();
	}

}
