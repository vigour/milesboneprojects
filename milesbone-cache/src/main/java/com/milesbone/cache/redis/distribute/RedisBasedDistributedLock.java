package com.milesbone.cache.redis.distribute;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.cache.redis.config.RedisConfiguration;
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
	protected  String key;

	/**
	 * 锁超时时间
	 */
	protected  long lockExpire;

	/**
	 * @throws IOException
	 * 
	 */
	public RedisBasedDistributedLock() throws IOException {
		this(null, null, 0, null);
	}

	
	
	
	/**
	 * @param jedisCluster
	 * @throws IOException 
	 */
	public RedisBasedDistributedLock(JedisCluster jedisCluster) throws IOException {
		this(jedisCluster, null, 0, null);
	}




	/**
	 * @param jedisCluster
	 * @param key
	 * @param expire
	 * @throws IOException
	 */
	public RedisBasedDistributedLock(JedisCluster jedisCluster, String key, long lockExpire, SocketAddress timeServerAddr)
			throws IOException {
		super();
		logger.debug("初始化redis分布式锁开始...");
		
		if(jedisCluster == null){
			logger.error("初始化redis分布式锁异常: jedisCluster参数未设置");
			throw new IllegalArgumentException("jedisCluster参数未设置");
		}
		this.jedisCluster = jedisCluster;
		
		Properties redisProp = RedisConfiguration.getInstance().getRedisProperties(); 
		
		if (StringUtils.isBlank(key)) {
			key = redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_LOCK_KEY, "redisLockKey");
		}
		this.key = key;
		
		if(lockExpire == 0){
			lockExpire = Long.parseLong(redisProp.getProperty(RedisConfiguration.REDIS_DEFAULT_LOCK_EXPIRE_TIME, "1000"));
		}
		this.lockExpire = lockExpire;
		
		if(timeServerAddr == null){
			timeClient = new TimeClient(timeServerAddr);
		}
		logger.debug("初始化redis分布式锁完成");
	}

	public boolean tryLock() {
		long lockExpireTime = serverTimeMillis() + lockExpire + 1;//锁超时时间
		String stringoflockExpireTime = String.valueOf(lockExpireTime);
		if(jedisCluster.setnx(key, stringoflockExpireTime) == 1){//成功获取到锁
			locked = true;
			setExclusiveOwnerThread(Thread.currentThread());
			return true;
		}
		
		String value = jedisCluster.get(key);
		if(value != null && isTimeExpired(value)){
			// 假设多个线程(非单jvm)同时走到这里
			String oldValue = jedisCluster.getSet(key, stringoflockExpireTime);
			// 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)  
            // 假如拿到的oldValue依然是expired的，那么就说明拿到锁了
			if(oldValue != null && isTimeExpired(oldValue)){
				locked = true;
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
		}else{
			// TODO lock is not expired, enter next loop retrying  
		}
		
		return false;
	}

	
	private boolean isTimeExpired(String value) {
		return Long.parseLong(value) < serverTimeMillis();
	}

	public void release() {
		try {
			jedisCluster.close();
			timeClient.close();
		} catch (Exception e) {
			logger.error("操作释放所有资源失败");
			e.printStackTrace();
		}
	}

	protected void releaseLock() {
		String value = jedisCluster.get(key);
		//TODO 判断锁是否过期
		if(StringUtils.isBlank(value)){
			return;
		}
		if(isTimeExpired(value)){
			doReleaseLock();
		}
	}

	private void doReleaseLock() {
		logger.debug("释放redis锁开始...");
		jedisCluster.del(key);
		logger.debug("释放redis 锁完成");
	}

	protected boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt)
			throws InterruptedException {
		if(interrupt){
			checkInterruption(); 
		}
		// 超时控制 的时间可以从本地获取, 因为这个和锁超时没有关系, 只是一段时间区间的控制  
		long start = this.localTimeMillis();
		
		long timeout = unit.toMillis(time);
		
		while (useTimeout ? isTimeout(start, timeout) : true) {
			if(interrupt){
				checkInterruption(); 
			}
			
			long lockExpireTime = serverTimeMillis() + lockExpire + 1;//锁超时时间
			String stringoflockExpireTime = String.valueOf(lockExpireTime);
			if(jedisCluster.setnx(key, stringoflockExpireTime) == 1){//成功获取到锁
				locked = true;
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			
			String value = jedisCluster.get(key);
			if(value != null && isTimeExpired(value)){
				// 假设多个线程(非单jvm)同时走到这里
				String oldValue = jedisCluster.getSet(key, stringoflockExpireTime);
				// 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)  
	            // 假如拿到的oldValue依然是expired的，那么就说明拿到锁了
				if(oldValue != null && isTimeExpired(oldValue)){
					locked = true;
					setExclusiveOwnerThread(Thread.currentThread());
					return true;
				}
			}else{
				// TODO lock is not expired, enter next loop retrying  
			}
		}
		
		
		return false;
	}
	
	
	
	
	private boolean isTimeout(long start, long timeout) {
		return start + timeout > localTimeMillis();
	}

	/**
	 * 判断线程是否被阻塞
	 * @throws InterruptedException
	 */
	private void checkInterruption() throws InterruptedException {
		if(Thread.currentThread().isInterrupted()){
			logger.error("线程已被阻塞{}", Thread.currentThread().getName());
			throw new InterruptedException();
		}
	}

	/**
	 * 获取服务器当前时间
	 * @return
	 */
	private long serverTimeMillis(){
		return timeClient.currentTimeMillis();
	}
	
	
	/**
	 * 获取本地服务器时间
	 * @return
	 */
	private long localTimeMillis(){
		return System.currentTimeMillis();
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

	public void setLockExpire(long lockExpire) {
		this.lockExpire = lockExpire;
	}
}
