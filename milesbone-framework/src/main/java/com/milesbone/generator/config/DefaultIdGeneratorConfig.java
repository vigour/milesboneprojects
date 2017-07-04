package com.milesbone.generator.config;

import com.milesbone.generator.IIdGeneratorConfig;



/*
 * 
 * ID生成器配置
 */
public class DefaultIdGeneratorConfig implements IIdGeneratorConfig {

	/**
	 * 
	 */
	public DefaultIdGeneratorConfig() {
		super();
	}

	public String getSpiltString() {
		return "-";
	}

	public int getInitial() {
		return 1;
	}

	public String getPrefix() {
		return "TEST";
	}

	public int getRollingInterval() {
		return 1;
	}

}
