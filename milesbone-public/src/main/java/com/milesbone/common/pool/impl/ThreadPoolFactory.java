package com.milesbone.common.pool.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;


/**
 * 线程池工厂
 * @author miles
 * @date 2017-04-15 下午11:09:39
 */
public class ThreadPoolFactory {

	private static Map<String, QueueThreadPool> pools;
	
	private Map<String, String> poolIdMap = null;
	
	public ThreadPoolFactory() {
	}
	
	/**
	 * 初始化线程池工厂
	 */
	public void init(){
		pools = new HashMap<String, QueueThreadPool>();
		if(poolIdMap != null){
			Set<String> keys = poolIdMap.keySet();
			QueueThreadPool pool = null;
			for (String key : keys) {
				String poolId = poolIdMap.get(key);
//				newPool(poolid,coreThreadNum,maxThreadNum,cachenum,keepalive);
			}
		}
	}

	public static ThreadPoolFactory getInstance(){
		return null;
	}
}
