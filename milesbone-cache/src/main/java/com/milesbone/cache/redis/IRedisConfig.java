package com.milesbone.cache.redis;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;

/**
 * redis配置接口
 * @author miles
 * @date 2017-05-06 下午7:33:50
 */
public interface IRedisConfig {

	/**
	 * 获取redis连接串
	 * @return
	 */
	String getRedisServers();

	/**
	 * 设置redis连接串
	 * @param redisServers
	 */
	void setRedisServers(String redisServers);

	/**
	 * 获取默认数据有效期 ，默认30天（2592000秒）
	 * @return
	 */
	int getDataExpireTime();

	/**
	 * 设置默认数据有效期 ，默认30天（2592000秒）
	 * @param dataExpireTime
	 */
	void setDataExpireTime(int dataExpireTime);

	/**
	 * 获取redis连接节点
	 * @return
	 */
	Set<HostAndPort> getNodes();

	/**
	 * 设置redis连接节点
	 * @param nodes
	 */
	void setNodes(Set<HostAndPort> nodes);

	/**
	 * 获取集群间最多重定向次数(默认5)
	 * @return
	 */
	int getMaxAttempts();

	/**
	 * 设置集群间最多重定向次数(默认5)
	 * @param maxAttempts
	 */
	void setMaxAttempts(int maxAttempts);

	
	/**
	 * 获取集群数据默认超时时间
	 * @return
	 */
	int getConnectionTimeout();

	/**
	 * 设置集群数据默认超时时间
	 * @param connectionTimeout
	 */
	void setConnectionTimeout(int connectionTimeout);

	/**
	 * 获取连接池配置
	 * @return
	 */
	GenericObjectPoolConfig getPoolconfig();

	/**
	 * 设置连接池配置
	 * @param poolconfig
	 */
	void setPoolconfig(GenericObjectPoolConfig poolconfig);
	

}
