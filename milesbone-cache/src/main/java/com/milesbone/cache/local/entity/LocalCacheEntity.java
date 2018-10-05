package com.milesbone.cache.local.entity;

import com.milesbone.cache.common.entity.ICacheEntity;

/**
 * 本地缓存实体类
 * @Title  LocalCacheEntity.java
 * @Package com.milesbone.cache.local.entity
 * @Description    TODO
 * @author miles
 * @date   2018-10-05 09:43
 */
public class LocalCacheEntity implements ICacheEntity{

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 4337884972373664844L;

	/**
	 * 缓存值
	 */
	private Object value;
	
	/**
	 * 保存时间戳
	 */
	private long generateTimestamp;
	
	/**
	 * 失效时间
	 */
	private long expireTime;
	
	
	
	public LocalCacheEntity() {
		super();
	}
	
	

	public LocalCacheEntity(Object value, long generateTimestamp, long expireTime) {
		super();
		this.value = value;
		this.generateTimestamp = generateTimestamp;
		this.expireTime = expireTime;
	}



	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#getGenerateTimestamp()
	 */
	public long getGenerateTimestamp() {
		return generateTimestamp;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#setGenerateTimestamp(long)
	 */
	public void setGenerateTimestamp(long generateTimestamp) {
		this.generateTimestamp = generateTimestamp;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#getExpireTime()
	 */
	public long getExpireTime() {
		return expireTime;
	}

	/* (non-Javadoc)
	 * @see com.milesbone.cache.local.entity.ILocalCacheEntity#setExpireTime(long)
	 */
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	
	

}
