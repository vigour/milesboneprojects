package com.milesbone.common.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程池配置
 * 
 * @author miles
 * @date 2017-04-27 下午2:52:24
 */
public class ThreadPoolConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolConfiguration.class);

	private static final String THREAD_POOL_CONFIG_PATH = "/threadpool.properties";
	
	private ThreadPoolConfiguration() {
		init();
	}

	// 线程池配置
	private Properties threadPoolPorp = null;

	public static ThreadPoolConfiguration getInstance() {
		return ThreadPoolConfigurationHolder.instance;
	}

	private static class ThreadPoolConfigurationHolder {
		private final static ThreadPoolConfiguration instance = new ThreadPoolConfiguration();
	}

	/**
	 * 初始化线程池配置文件
	 */
	private void init() {
		logger.debug("初始化线程池配置文件");
		// 加载线程池配置文件
		if (threadPoolPorp == null) {
			try{
				threadPoolPorp = new Properties();
				InputStream in = this.getClass().getResourceAsStream(THREAD_POOL_CONFIG_PATH);
				threadPoolPorp.load(in);
				in.close();
				
			}catch(Exception e){
				logger.error("加载线程池配置文件失败");
				e.printStackTrace();
			}
		}
		logger.debug("初始化线程池配置文件完成");
	}

	/**
	 * 线程池类型
	 * 
	 * @author miles
	 * @date 2017-04-27 下午2:31:07
	 */
	public enum CommonThreadPoolType {
		/**
		 * 通用线程池
		 */
		COMMON,
	}

	/**
	 * 默认线程池参数配置
	 * 
	 * @author miles
	 * @date 2017-04-27 下午4:13:44
	 */
	public enum Configuration {
		
		/**
		 * 默认核心线程数
		 */
		THREAD_POOL_CORE_SIZE("thread.pool.core.size", "10"),

		/**
		 * 默认最大线程数
		 */
		THREAD_POOL_MAX_THREAD_SIZE("thread.pool.max.size", "50"),

		/**
		 * 默认缓存线程数即队列大小
		 */
		THREAD_POOL_CACHE_SIZE("thread.pool.cache.size", "500"),

		/**
		 * 默认最长存活时间
		 */
		THREAD_POOL_KEEPALIVE("thread.pool.keepAlive", "600000"),

		/**
		 * 默认策略
		 */
		THREAD_POOL_POLICY("thread.pool.policy", ThreadPoolPolicy.CallerRunsPolicy.getValue());

		private String key;

		private String value;

		/**
		 * @param key
		 * @param value
		 */
		private Configuration(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}

	
	/**
	 * 线程池策略
	 * @author miles
	 * @date 2017-04-27 下午4:26:11
	 */
	public enum ThreadPoolPolicy {

		/**
		 * 由调用线程处理该任务
		 */
		CallerRunsPolicy("1"),

		/**
		 * 丢弃任务并抛出RejectedExecutionException异常。 
		 */
		AbortPolicy("2"),

		/**
		 * 丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
		 */
		DiscardOldestPolicy("3"),

		/**
		 * 也是丢弃任务，但是不抛出异常。 
		 */
		DiscardPolicy("4");

		private String value;

		private ThreadPoolPolicy(String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}

	public Properties getThreadPoolPorp() {
		return threadPoolPorp;
	}

}
