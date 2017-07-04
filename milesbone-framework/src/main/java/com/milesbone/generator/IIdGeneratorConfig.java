package com.milesbone.generator;


/**
 * Id 生成器的配置接口
 * @author miles
 * @date 2017-04-09 下午9:41:31
 */
public interface IIdGeneratorConfig {

	/**
	 * 获取分隔符
	 * @return
	 */
	String getSpiltString();
	
	/**
	 * 获取初始值
	 * @return
	 */
	int getInitial();
	
	/**
	 * 获取前缀
	 * @return
	 */
	String getPrefix();
	
	/**
	 * 获取滚动间隔
	 * @return
	 */
	int getRollingInterval();
}
