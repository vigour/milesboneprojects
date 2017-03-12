package com.milesbone.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置文件工具类
 * @author miles
 * @date 2017-03-08 下午11:00:17
 */
public class PropertyFileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertyFileUtil.class);
	
	
	public static Properties load(String path){
		logger.debug("加载Properties文件方法");
		if(StringUtils.isBlank(path)){
			logger.error("加载Properties文件方法失败:文件路径为空 ");
			throw new NullPointerException("文件路径为空");
		}
		Properties properties = new Properties();
		InputStream inStream = null;
		try {
			inStream = PropertyFileUtil.class.getResourceAsStream(path);
			properties.load(inStream);
			inStream.close();
		} catch (IOException e) {
			logger.error("加载Properties文件内容方法失败:" + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("加载Properties文件方法结束");
		return properties;
	}
}
