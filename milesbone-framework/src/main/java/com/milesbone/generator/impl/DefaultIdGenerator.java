package com.milesbone.generator.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.generator.IIdGenerator;
import com.milesbone.generator.IIdGeneratorConfig;
import com.milesbone.generator.config.DefaultIdGeneratorConfig;



/**
 * 默认的ID生成器, 采用前缀+时间+原子数的形式实现 建议相同的配置采用同一个实例
 * 
 * @author miles
 * @date 2017-04-09 下午9:48:34
 */
public class DefaultIdGenerator implements IIdGenerator, Runnable {

	
	private static final Logger logger = LoggerFactory.getLogger(DefaultIdGenerator.class);	
	
	private String time;

	private AtomicInteger value;

	private static final DateFormat YMDHMS_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");

	private IIdGeneratorConfig config;

	private Thread thread;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * 
	 */
	public DefaultIdGenerator() {
		this(null);
	}

	/**
	 * @param config
	 */
	public DefaultIdGenerator(IIdGeneratorConfig config) {
		super();
		
		logger.debug("初始化默认ID生成器开始...");
		if(config == null){
			config = new DefaultIdGeneratorConfig();
			logger.debug("默认ID生成器配置为空,默认生成一个配置");
		}
		this.config = config;

		time = YMDHMS_FORMATTER.format(new Date());
		value = new AtomicInteger(config.getInitial());

		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
		logger.debug("初始化默认ID生成器完成");
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000 * config.getRollingInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String now = YMDHMS_FORMATTER.format(new Date());
//			if (!now.equals(time)) {
				lock.writeLock().lock();
				time = now;
//				value.set(config.getInitial());
				lock.writeLock().unlock();
//			}

		}

	}

	@Override
	public String next() {
		lock.readLock().lock();
		StringBuffer sb = new StringBuffer(config.getPrefix());
		sb.append(config.getSpiltString());
//		sb.append(time);
		
		sb.append(config.getSpiltString()).append(String.format("%08d", value.getAndIncrement()));
		lock.readLock().unlock();
		return sb.toString();
	}

}
