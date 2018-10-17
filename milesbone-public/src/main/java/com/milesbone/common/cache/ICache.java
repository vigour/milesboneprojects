package com.milesbone.common.cache;

import java.util.Set;

/**
 * 缓存接口定义
 * @author miles
 * @date 2017-03-26 上午9:07:28
 */
public interface ICache<T extends Object> {

	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value 必须实现java.io.Serializable
	 * @return true 添加成功   false 添加失败：如果缓存中已存在同名Key，则不执行添加操作，
	 */
	public boolean putCache(String key,T value);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value	必须实现java.io.Serializable
	 * @param expireTime 数据有效期 单位：秒
	 * @return 添加成功   false 添加失败：如果缓存中已存在同名Key，则不执行添加操作，
	 */
	public boolean putCache(String key,T value,long expireTime);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value 必须实现java.io.Serializable
	 * @return	true 添加成功 ,如果缓存中已存在同名Key，则执行覆盖， flase 添加失败
	 */
	public boolean putOrReplace(String key,T value);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value 必须实现java.io。Serializable
	 * @param expireTime  数据有效期 单位：秒
	 * @return true 添加成功 ,如果缓存中已存在同名Key，则执行覆盖， flase 添加失败
	 */
	public boolean putOrReplace(String key,T value,long expireTime);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value 必须实现java.io.Serializable
	 * @return true 更新操作，如果缓存中存在同名Key则执行更新值，但原值有效期不变被新值继承， flase 更新失败：同名key不存在或无法连接服务器
	 */
	public boolean replace(String key,T value);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @param value 必须实现java.io.Serializable
	 * @param expireTime 数据有效期 单位：秒
	 * @return	更新操作，如果缓存中存在同名Key则执行更新和有效期， flase 更新失败：同名key不存在或无法连接服务器
	 */
	public boolean replace(String key, T value,long expireTime);
	
	/**
	 * @param key 键值，最好小于250k,中间不包含空格和换行符，否则在memcache中会出异常
	 * @return （memcache返回Oject，redis返回JSONObject）
	 * --从缓存中获取Key值的对象
	 */
	public T getCache(String key);
	
	/**
	 * @param key 键值
	 * @return true key在缓存中已存在，并删除成功   false：key在缓存中不存在或删除失败  
	 * 移除缓存中key值对应的对象
	 */
	public boolean remove(String key);
	
	/**
	 * 移除所有缓存
	 * @param key 键值
	 * @return true key在缓存中已存在，并删除成功   false：key在缓存中不存在或删除失败  
	 * 移除缓存中key值对应的对象
	 */
	public boolean clearAll();
	
	/**
	 * @param key 键值
	 * @return true key在缓存中存在  false key在缓存中不存在
	 */
	public boolean contains(String key);
	
	 /**
     * 缓存是否超时失效
     * @param key
     * @return
     */
    public boolean isExpired(String key);
    
    /**
     *KEYS pattern
	 *查找所有符合给定模式 pattern 的 key 。
	 *KEYS * 匹配数据库中所有 key 。
	 *KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
	 *KEYS h*llo 匹配 hllo 和 heeeeello 等。
	 *KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
	 *特殊符号用 \ 隔开
     * @return
     */
    public Set<String> getAllKeys();

	
}