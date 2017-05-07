package com.milesbone.cache.redis.distribute;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.clinet.impl.TimeClient;
import com.milesbone.common.distribute.lock.AbstractLock;

import redis.clients.jedis.JedisCluster;

/**
 * 基于Redis的SETNX操作实现的非重入式分布式锁
 * 
 * @author miles
 * @date 2017-05-01 下午2:59:36
 */
public class RedisBasedDistributedLock extends AbstractLock {

	private static final Logger logger = LoggerFactory.getLogger(RedisBasedDistributedLock.class);

	/**
	 * jedis集群操作对象
	 */
	private JedisCluster jedisCluster;

	/**
	 * 时间服务器客户端
	 */
	private TimeClient timeClient;

	/**
	 * 锁的key
	 */
	private String key;

	/**
	 * 锁超时时间
	 */
	private long expire;

	/**
	 * @throws IOException
	 * 
	 */
	public RedisBasedDistributedLock() throws IOException {
		this(null, null, 0, null);
	}

	/**
	 * @param jedisCluster
	 * @param key
	 * @param expire
	 * @throws IOException
	 */
	public RedisBasedDistributedLock(JedisCluster jedisCluster, String key, long expire, SocketAddress timeServerAddr)
			throws IOException {
		super();
		logger.debug("初始化redis分布式锁开始...");
		if (StringUtils.isBlank(key)) {
			
		}
		this.key = key;
		this.jedisCluster = jedisCluster;
		this.expire = expire;
		timeClient = new TimeClient(timeServerAddr);
		logger.debug("初始化redis分布式锁完成");
	}

	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	public void release() {
		// TODO Auto-generated method stub

	}

	protected void releaseLock() {
		// TODO Auto-generated method stub

	}

	protected boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt)
			throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public final void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public final void setTimeClient(TimeClient timeClient) {
		this.timeClient = timeClient;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final void setExpire(long expire) {
		this.expire = expire;
	}

}
