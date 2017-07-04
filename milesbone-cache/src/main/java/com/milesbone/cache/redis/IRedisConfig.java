package com.milesbone.cache.redis;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;

/**
 * redis配置接口
 * 
 * @author miles
 * @date 2017-05-06 下午7:33:50
 */
public interface IRedisConfig {
	
	/**
	 * IP地址和端口正则
	 */
	public static final Pattern IP_AND_PORT_PATTERN = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

	/**
	 * 获取redis连接节点
	 * 
	 * @return
	 */
	Set<HostAndPort> getNodes();

	/**
	 * 设置redis连接节点
	 * 
	 * @param nodes
	 */
	void setNodes(Set<HostAndPort> nodes);

	/**
	 * 获取集群数据默认超时时间
	 * 
	 * @return
	 */
	Integer getConnectionTimeout();

	/**
	 * 设置集群数据默认超时时间
	 * 
	 * @param connectionTimeout
	 */
	void setConnectionTimeout(Integer connectionTimeout);

	/**
	 * 获取默认数据有效期 ，默认30天（2592000秒）
	 * 
	 * @return
	 */
	Integer getDataExpireTime();

	/**
	 * 设置默认数据有效期 ，默认30天（2592000秒）
	 * 
	 * @param dataExpireTime
	 */
	void setDataExpireTime(Integer dataExpireTime);

	/**
	 * 获取集群间最多重定向次数(默认5)
	 * 
	 * @return
	 */
	Integer getMaxAttempts();

	/**
	 * 设置集群间最多重定向次数(默认5)
	 * 
	 * @param maxAttempts
	 */
	void setMaxAttempts(Integer maxAttempts);

	/**
	 * 获取连接池配置
	 * 
	 * @return
	 */
	GenericObjectPoolConfig getPoolconfig();

	/**
	 * 设置连接池配置
	 * 
	 * @param poolconfig
	 */
	void setPoolconfig(GenericObjectPoolConfig poolconfig);

}
