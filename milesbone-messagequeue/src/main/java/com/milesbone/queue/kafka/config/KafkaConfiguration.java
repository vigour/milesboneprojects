package com.milesbone.queue.kafka.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.util.PropertyFileUtil;

/**
 * 生产者和消费者配置文件加载
 * 
 * @author miles
 * @date 2016-08-22 下午10:17:11
 */
public class KafkaConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(KafkaConfiguration.class);
	
	private final static String COMSUMER_CONFIG_PAYH = "/consumer.properties"; 
	
	private final static String PRODUCER_CONFIG_PAYH = "/producer.properties"; 
	
	private final static String KAFKA_TOOLS_CONFIG_PAYH = "/kafkatools.properties"; 
	
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
			consummerProp = PropertyFileUtil.load(COMSUMER_CONFIG_PAYH);
		}
		logger.debug("初始化kafka生产者配置文件完成");

		// 加载生产者配置文件
		logger.debug("初始化kafka消费者配置文件");
		if (producerProp == null) {
			producerProp = PropertyFileUtil.load(PRODUCER_CONFIG_PAYH);
		}
		logger.debug("初始化kafka消费者配置文件完成");
		
		// 加载自定义配置文件
		logger.debug("初始化kafka自定义配置文件");
		if (kafkaProp == null) {
			kafkaProp = PropertyFileUtil.load(KAFKA_TOOLS_CONFIG_PAYH);
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
