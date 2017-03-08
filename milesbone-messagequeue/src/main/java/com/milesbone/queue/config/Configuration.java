package com.milesbone.queue.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * 生产者和消费者配置文件加载
 * 
 * @author miles
 * @date 2016-08-22 下午10:17:11
 */
public class Configuration {

	// 消费者属性
	private Properties consummerProp = null;

	// 生产者属性
	private Properties producerProp = null;
	
	//zookeeper属性
	private  Properties zookeeperProp = null;

	private static final Configuration instance = new Configuration();

	public static Configuration getInstance() {
		return instance;
	}

	private Configuration() {
		init();
	}

	private void init() {
		// 加载消费者配置文件
		if (consummerProp == null) {
			try {
				consummerProp = new Properties();
				InputStream in = this.getClass().getResourceAsStream("/consumer.properties");
				consummerProp.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 加载生产者配置文件
		if (producerProp == null) {
			try {
				producerProp = new Properties();
				InputStream in = this.getClass().getResourceAsStream("/producer.properties");
				producerProp.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 加载zookeeper配置文件
		if (zookeeperProp == null) {
			try {
				zookeeperProp = new Properties();

				InputStream in = this.getClass().getResourceAsStream("/zookeeper.properties");

				zookeeperProp.load(in);

				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public Properties getConsummerProp() {
		return consummerProp;
	}

	public Properties getProducerProp() {
		return producerProp;
	}

}
