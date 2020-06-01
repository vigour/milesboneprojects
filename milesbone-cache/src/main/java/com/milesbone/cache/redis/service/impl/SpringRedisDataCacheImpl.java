package com.milesbone.cache.redis.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.milesbone.cache.redis.service.IRedisCache;


/**
 * Spring data 管理 redis 服务
 * @Title  SpringRedisDataCacheImpl.java
 * @Package com.milesbone.cache.redis.service.impl
 * @Description    TODO
 * @author miles
 * @date   2018-10-12 16:31
 */
public class SpringRedisDataCacheImpl implements IRedisCache,Cache{
	
	private static final Logger logger = LoggerFactory.getLogger(SpringRedisDataCacheImpl.class);
	
	
	private String name;
	
	private Long expireTime = 0L;
	
	
	/*** 无容量限制key带时效性 */
	private RedisTemplate<String, Object> redisTemplate;

	public boolean putCache(String key, String value) {
		return putCache(key, value, expireTime);
	}

	public boolean putCache(String key, String value, long expireTime) {
		redisTemplate.opsForValue().set(key, value, expireTime,TimeUnit.SECONDS);
		return true;
	}

	public boolean putOrReplace(String key, String value) {
		return putOrReplace(key, value,expireTime);
	}

	public boolean putOrReplace(String key, String value, long expireTime) {
		redisTemplate.opsForValue().setIfAbsent(key, value);
		return true;
	}

	public boolean replace(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean replace(String key, String value, long expireTime) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getCache(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean clearAll() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean contains(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isExpired(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set<String> getAllKeys() {
		return null;
	}

	public String getName() {
		return this.name;
	}

	public Object getNativeCache() {
		return this;
	}

	public ValueWrapper get(Object key) {
		final String keyStr = name + key.toString();
		Object objectValue = redisTemplate.execute(new RedisCallback<Object>() {

			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					byte[] key = keyStr.getBytes("utf-8");
					byte[] value = connection.get(key);
					if(value != null) {
						if(expireTime > 0) {
							connection.expire(key, expireTime);
						}
						return byteArrayToObjct(value);//
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return null;
			}
		}, true);
		logger.debug("获取自定义缓存对象完成");
		return (objectValue != null ? new SimpleValueWrapper(objectValue) : null);
	}
	
	
	/**
	 * 相当于对象的序列化
	 * @param bytes
	 * @return
	 */
	private Object byteArrayToObjct(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException e) {
			logger.error("redis byte convert to Object fail :{}",e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger.error("redis byte convert to Object fail :ClassNotFoundException");
			e.printStackTrace();
		}
		return obj;
	}
	
	
	/**
	 * 相当于对象的序列化
	 * @param obj
	 * @return
	 */
	private byte[] objectToByteArray(Object object) {
		byte[] bytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return bytes;
	}


	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		if(StringUtils.isBlank(String.valueOf(key)) || null == type) {
			return null;
		}else {
			final Class<T> finalType = type;
			final Object object = this.get(key);
			if(finalType != null && finalType.isInstance(object) && null != object) {
				return (T) object;
			}
		}
		return null;
	}

	
	/**
	 * 自定义缓存赋值
	 */
	public void put(Object key, Object value) {
		final String keyStr = key.toString();
		final Object valueStr = value;
		redisTemplate.execute(new RedisCallback<Long>()  {

			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					byte[] bytekey = keyStr.getBytes("utf-8");
					byte[] valueKey = objectToByteArray(valueStr);
					connection.set(bytekey, valueKey);
					if(expireTime > 0) {
						connection.expire(bytekey, expireTime);
					}
					return 1L;
				} catch (UnsupportedEncodingException e) {
					logger.error("自定义缓存put()方法失败:{}",e.getMessage());
					e.printStackTrace();
				}
				return 0L;
			}
			
		},true);
	}

	public ValueWrapper putIfAbsent(Object key, Object value) {
		if(StringUtils.isBlank(String.valueOf(key))  || StringUtils.isBlank(String.valueOf(value))) {
			return null;
		}
		this.put(key, value);
		return new SimpleValueWrapper(value);
	}

	public void evict(Object key) {
		final String keyStr = key.toString();
		redisTemplate.execute(new RedisCallback<Long>() {

			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					byte[] bytekey = keyStr.getBytes("utf-8");
					return connection.del(bytekey);
				} catch (UnsupportedEncodingException e) {
					logger.error("执行缓存evict()方法失败:{}",e.getMessage());
					e.printStackTrace();
				}
				return 0L;
			}
		}, true);
	}

	public void clear() {
		redisTemplate.execute(new RedisCallback<String>() {

			public String doInRedis(RedisConnection connection) throws DataAccessException {
				connection.flushDb();
				return "clear finsh";
			}
		},true);
	}

	public boolean hset(String key, String field, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public String hget(String key, String field) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hexists(String key, String field) {
		// TODO Auto-generated method stub
		return false;
	}

	public long hdel(String key, String field) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long hlen(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<String> hkeys(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> hvals(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> hgetAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hset(byte[] key, byte[] field, byte[] value) {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] hget(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hexists(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		return false;
	}

	public long hdel(byte[] key, byte[] field) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long hlen(byte[] key) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<byte[]> hkeys(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<byte[]> hvals(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<byte[], byte[]> hgetAll(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(byte[] key) {
		// TODO Auto-generated method stub
		return false;
	}

	public Long ttl(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long ttl(byte[] key) {
		// TODO Auto-generated method stub
		return null;
	}

	public void expire(String key, int seconds) {
		// TODO Auto-generated method stub
		
	}

	public void expire(byte[] key, int seconds) {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

}
