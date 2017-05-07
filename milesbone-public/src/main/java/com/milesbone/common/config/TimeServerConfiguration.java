package com.milesbone.common.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间服务器配置
 * @author miles
 * @date 2017-05-01 下午7:52:45
 */
public class TimeServerConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(TimeServerConfiguration.class);
	
	private static final String TIME_SERVER_CONFIG_PATH = "/timeserver.properties";
	
	private Properties timeServerProp = null;//时间服务器的配置
	
	
	/**
	 * 
	 */
	private TimeServerConfiguration() {
		init();
	}

	public static TimeServerConfiguration getInstance(){
		return TimeServerConfigurationHolder.instance;
	}
	
	private static class TimeServerConfigurationHolder{
		private static final TimeServerConfiguration instance = new TimeServerConfiguration();
	}
	
	/**
	 * 初始化
	 */
	private void init() {
		logger.debug("初始化时间服务器配置文件");
		//加载时间服务器配置文件
		if(timeServerProp == null){
			try {
				timeServerProp = new Properties();
				InputStream in = this.getClass().getResourceAsStream(TIME_SERVER_CONFIG_PATH);
				timeServerProp.load(in);
			} catch (Exception e) {
				logger.error("加载时间服务器配置文件失败");
				e.printStackTrace();
			}
		}
		logger.debug("初始化时间服务器配置完成");
	}
	
	/**
	 * 配置文件key值
	 * @author miles
	 * @date 2017-05-01 下午11:41:30
	 */
	public enum Configuration{
		/**
		 * 默认的主机key
		 */
		DEFAULT_HOST_NAME("time.server.hostname","localhost"),
		
		/**
		 * 默认的端口key
		 */
		DEFAULT_PORT("time.server.port","19999");
		
		private String key;
		
		private String value;
		
		private Configuration(String key, String value){
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
	

	public Properties getTimeServerProp() {
		return timeServerProp;
	}
	
}
