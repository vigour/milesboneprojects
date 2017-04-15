package com.milesbone.queue.kafka.util;

import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.kafka.config.KafkaConfiguration;


/**
 * Kafka工具类
 * @author miles
 * @date 2017-03-18 下午8:17:43
 */
public class KafkaUtil {

	private static final Logger logger = LoggerFactory.getLogger(KafkaUtil.class);

	private Properties producerProp;
	
	private Properties consumerProp;
	
	private Properties kafkatoolsProp;
	
	private KafkaProducer<String, String> kafkaProducer;
	
	private KafkaConsumer<String, String> kafkaConsumer;
	private KafkaUtil() {
		init();
	}

	public static KafkaUtil getInstance(){
		return KafkaUtilHolder.instance;
	}
	
	private static class KafkaUtilHolder{
		private final static KafkaUtil instance = new KafkaUtil();
	}
	
	private void init() {
		logger.debug("初始化kafka工具类......");
		
		producerProp = KafkaConfiguration.getInstance().getProducerProp();
		
		consumerProp = KafkaConfiguration.getInstance().getConsummerProp();
		
		kafkatoolsProp = KafkaConfiguration.getInstance().getKafkaProp();
		
		logger.debug("初始化kafka工具类完成");
	}
	
	public KafkaProducer<String, String> getProducer(){
		kafkaProducer = new KafkaProducer<>(producerProp);
		return kafkaProducer;
	}
	
	public KafkaConsumer<String, String> getConsumer(){
		kafkaConsumer = new KafkaConsumer<>(consumerProp);
		return kafkaConsumer;
	}

	
	
	public Properties getKafkatoolsProp() {
		return kafkatoolsProp;
	}
}
