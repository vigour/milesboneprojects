package com.milesbone.generator.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.distribute.ILock;
import com.milesbone.generator.IIdGenerator;
import com.milesbone.generator.IIdGeneratorConfig;
import com.milesbone.generator.config.DefaultIdGeneratorConfig;

public class DistributeLockIdGenerator implements IIdGenerator, Runnable{

	private static final Logger LOGGER = LoggerFactory.getLogger(DistributeLockIdGenerator.class);
	
	private IIdGeneratorConfig config;
	
	private Thread thread;

	private ILock lock;
	
	private AtomicInteger value;
	
	
	private volatile boolean isStop;
	
	
	/**
	 * 
	 */
	public DistributeLockIdGenerator() {
		this(null);
	}
	
	
	
	
	

	/**
	 * @param config
	 */
	public DistributeLockIdGenerator(IIdGeneratorConfig config) {
		this(config,null);
	}





	/**
	 * @param config
	 * @param lock
	 */
	public DistributeLockIdGenerator(IIdGeneratorConfig config, ILock lock) {
		super();

		LOGGER.debug("初始化Redis Lock ID生成器开始...");
		if(config == null){
			config = new DefaultIdGeneratorConfig();
			LOGGER.debug("ID生成器配置为空,默认生成一个配置");
		}
		this.config = config;
		
		if(lock == null){
			//TODO
//			lock = new ReentrantReadWriteLock();
			LOGGER.debug("ID生成器锁为空,默认生成一个锁");
		}
		this.lock = lock;
		
		value = new AtomicInteger(config.getInitial());
		
		LOGGER.debug("初始化Redis Lock ID生成器完成");
	}





	@Override
	public void run() {
		LOGGER.debug("开始运行ID生成器线程");
		while(isStop){
			String id = next();
			LOGGER.info("获取到新的ID:{}",id);
		}
		
		LOGGER.debug("开始运行ID生成器线程");
	}

	@Override
	public String next() {
		if(lock.tryLock(30,TimeUnit.SECONDS)){
			try {
				LOGGER.info(Thread.currentThread().getName() + " get lock");
				StringBuffer sb = new StringBuffer(config.getPrefix());
				sb.append(config.getSpiltString());
				sb.append(config.getSpiltString());
				sb.append(String.format("%08d", value.getAndIncrement()));
				return sb.toString();
			} finally {
				lock.unlock();
			}
		}
		return null;
	}
	
	

}
