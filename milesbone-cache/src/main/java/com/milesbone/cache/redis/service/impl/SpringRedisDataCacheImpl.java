package com.milesbone.cache.redis.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.Cache;

import com.milesbone.cache.redis.service.IRedisCache;

public class SpringRedisDataCacheImpl implements IRedisCache,Cache{

	public boolean putCache(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean putCache(String key, String value, long expireTime) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean putOrReplace(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean putOrReplace(String key, String value, long expireTime) {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getNativeCache() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueWrapper get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T get(Object key, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	public void put(Object key, Object value) {
		// TODO Auto-generated method stub
		
	}

	public ValueWrapper putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void evict(Object key) {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
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
