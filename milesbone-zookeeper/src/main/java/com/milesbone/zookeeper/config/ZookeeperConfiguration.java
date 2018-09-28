package com.milesbone.zookeeper.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.util.PropertyFileUtil;
import com.milesbone.zookeeper.IZookeeperConfig;


public class ZookeeperConfiguration implements IZookeeperConfig{
	
	private static  final Logger logger = LoggerFactory.getLogger(ZookeeperConfiguration.class);

	/**
	 * zookeeper 属性加载
	 */
	private Properties zookeeperProp = null;
	
	private static final String ZOOKEEPER_CONFIG_PATH = "/zookeeper.properties";
	
	private static final ZookeeperConfiguration instance = new ZookeeperConfiguration();
	
	public static ZookeeperConfiguration getInstance(){
		return instance;
	}
	
	private ZookeeperConfiguration(){
		init();
	}


	private void init() {
		logger.debug("zookeeper配置文件");
		if(zookeeperProp == null){
			zookeeperProp = PropertyFileUtil.load(ZOOKEEPER_CONFIG_PATH);
		}
		logger.debug("zookeeper配置文件完成");
	}

	public Properties getZookeeperProp() {
		return zookeeperProp;
	}

	public Properties getProp() {
		return getZookeeperProp();
	}

	
}
