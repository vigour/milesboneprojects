package com.milesbone.queue.pool;

import java.util.Map;

public class ThreadPoolFactory {

	private static Map<String, QueueThreadPool> pools;
	
	private Map<String, String> poolIdMap = null;
	
	public ThreadPoolFactory() {
	}

	public static ThreadPoolFactory getInstance(){
		return null;
	}
}
