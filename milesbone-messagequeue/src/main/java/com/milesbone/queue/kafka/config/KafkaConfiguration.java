package com.milesbone.queue.kafka.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产者和消费者配置文件加载
 * 
 * @author miles
 * @date 2016-08-22 下午10:17:11
 */
public class KafkaConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(KafkaConfiguration.class);
	
	// 消费者属性
	private Properties consummerProp = null;

	// 生产者属性
	private Properties producerProp = null;
	
	//自定义属性
	private Properties kafkaProp = null;

	private static final KafkaConfiguration instance = new KafkaConfiguration();

	/**
	 * 默认消费者topic
	 */
	public static final String CONSUMMER_DEFAULT_TOPIC = "kafka.consummer.default.topic";
	
	/**
	 * 消费者缓存条数
	 */
	public static final String CONSUMMER_BATCH_SIZE = "kafka.consummer.batch.size";
	
	/**
	 * 消费者批量回调开关
	 */
	public static final String CONSUMMER_BATCH_CALLBACK_MODE = "kafka.consummer.enable.batch.callback";
	
	public static KafkaConfiguration getInstance() {
		return instance;
	}

	private KafkaConfiguration() {
		init();
	}

	private void init() {
		logger.debug("初始化kafka生产者配置文件");
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
		logger.debug("初始化kafka生产者配置文件完成");

		// 加载生产者配置文件
		logger.debug("初始化kafka消费者配置文件");
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
		logger.debug("初始化kafka消费者配置文件完成");
		
		// 加载自定义配置文件
		logger.debug("初始化kafka自定义配置文件");
		if (kafkaProp == null) {
			try {
				kafkaProp = new Properties();
				InputStream in = this.getClass().getResourceAsStream("/kafkatools.properties");
				kafkaProp.load(in);
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.debug("初始化kafka自定义配置文件完成");

	}

	public Properties getConsummerProp() {
		return consummerProp;
	}

	public Properties getProducerProp() {
		return producerProp;
	}

	public Properties getKafkaProp() {
		return kafkaProp;
	}

}
