package com.milesbone.common.pool.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.config.ThreadPoolConfiguration;
import com.milesbone.common.config.ThreadPoolConfiguration.Configuration;
import com.milesbone.common.config.ThreadPoolConfiguration.CommonThreadPoolType;



/**
 * 线程池工厂
 * @author miles
 * @date 2017-04-15 下午11:09:39
 */
public class ThreadPoolFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

	private static Map<String, QueueThreadPool> pools;
	
	private Map<String, String> poolIdMap = null;
	
	public ThreadPoolFactory() {
	}
	
	/**
	 * 初始化线程池工厂
	 */
	public void init(){
		logger.debug("初始化线程池工厂");
		pools = new ConcurrentHashMap<String, QueueThreadPool>();
		
		int coreThreadNum = 0;//默认核心线程数
		int maxThreadNum = 0;//默认最大线程数
		int cacheNum = 0;//默认缓存线程数
		int keepAlive = 1000*60*10; //默认最长存活时间
		
		
		if(poolIdMap != null){
			Set<String> keys = poolIdMap.keySet();
			for (String key : keys) {
				String poolId = poolIdMap.get(key);
				createPool(poolId, coreThreadNum, maxThreadNum, cacheNum, keepAlive);
			}
		}else{
			Properties prop = ThreadPoolConfiguration.getInstance().getThreadPoolPorp();
			coreThreadNum = Integer.parseInt(prop.getProperty(Configuration.THREAD_POOL_CORE_SIZE.getKey(), Configuration.THREAD_POOL_CORE_SIZE.getValue()));
			maxThreadNum = Integer.parseInt(prop.getProperty(Configuration.THREAD_POOL_MAX_THREAD_SIZE.getKey(), Configuration.THREAD_POOL_MAX_THREAD_SIZE.getValue()));
			cacheNum = Integer.parseInt(prop.getProperty(Configuration.THREAD_POOL_CACHE_SIZE.getKey(), Configuration.THREAD_POOL_CACHE_SIZE.getValue()));
			keepAlive = Integer.parseInt(prop.getProperty(Configuration.THREAD_POOL_KEEPALIVE.getKey(), Configuration.THREAD_POOL_KEEPALIVE.getValue()));
			
			CommonThreadPoolType[] types = CommonThreadPoolType.values();
			for(CommonThreadPoolType type:types){
				createPool(type.name(), coreThreadNum, maxThreadNum, cacheNum, keepAlive);
			}
		}
		logger.debug("初始化线程池工厂完成");
	}
	
	

	/**
	 * 创建线程池
	 * @param poolId
	 * @param coreThreadNum
	 * @param maxThreadNum
	 * @param cacheNum
	 * @param keepAlive
	 * @return
	 */
	public synchronized QueueThreadPool createPool(String poolId, int coreThreadNum, int maxThreadNum, int cacheNum, int keepAlive) {
		if(pools.containsKey(poolId)){
			return pools.get(poolId);
		}
		QueueThreadPool pool = new QueueThreadPool();
		pool.init(poolId, coreThreadNum, maxThreadNum, keepAlive);
		pools.put(poolId, pool);
		return pool;
	}

	/**
	 * 通过Id 获取线程池
	 * @param id
	 * @return
	 */
	public static QueueThreadPool getPool(String poolId){
		return pools.get(poolId);
	}
	
	
	/**
	 * 关闭线程池
	 */
	public static void stopPool(){
		synchronized (pools) {
			Set<String> set = pools.keySet();
			Iterator<String> it = set.iterator();
			
			while(it.hasNext()){
				String key = it.next();
				pools.get(key).stop();
			}
		}
	}
}
