package com.milesbone.common.config;

import java.util.Properties;

/**
 * 	配置接口
 * @Title  IConfiguration.java
 * @Package com.milesbone.common.config
 * @Description    
 * @author miles
 * @date   2018-09-27 10:34
 */
public interface IConfiguration{

	/**
	 * 获取默认的Properties配置
	 * @return
	 */
	public Properties getProp();
}
