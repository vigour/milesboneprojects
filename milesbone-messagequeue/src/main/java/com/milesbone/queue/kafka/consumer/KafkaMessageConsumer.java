package com.milesbone.queue.kafka.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.kafka.config.KafkaConfiguration;
import com.milesbone.queue.kafka.listener.IKafkaListener;


/**
 * kafka消费者线程组
 * @author miles
 * @date 2017-03-25 下午3:19:59
 */
public class KafkaMessageConsumer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);
	
	private String topic;// 主题
	
	private String groupId;// groupId
	
	private int num;//消费者线程个数

	private Properties consumerProp;// 消费者参数配置
	
	private Properties kafkatoolsProp;//kafka自定义参数配置
	
	private IKafkaListener listener;// 消息回调
	
	private AtomicBoolean runFlag = new AtomicBoolean(false);
	
	private ExecutorService executorService;// consumer线程池 
	
	private List<KafkaConsumerRunner> list = new ArrayList<>();
	
	public KafkaMessageConsumer() {
		this(null,null,1,null,null);
	}
	
	
	public KafkaMessageConsumer(String topic, String groupId, int num) {
		this(topic,groupId,num,null,null);
	}


	public KafkaMessageConsumer(String topic, String groupId, int num, Properties consumerProp) {
		this(topic,groupId,num,consumerProp,null);
	}


	public KafkaMessageConsumer(String topic, String groupId, int num, Properties consumerProp,
			IKafkaListener listener) {
		super();
		logger.debug("开始初始化kafka消息组类");
		this.num = num;
		if(null == consumerProp){
			consumerProp = KafkaConfiguration.getInstance().getConsummerProp();
		}
		consumerProp.putAll(consumerProp);
		this.consumerProp = consumerProp;
		
		if(kafkatoolsProp == null){
			kafkatoolsProp = KafkaConfiguration.getInstance().getKafkaProp();
		}
		
		if(StringUtils.isBlank(topic)){
			topic = kafkatoolsProp.getProperty(KafkaConfiguration.CONSUMMER_DEFAULT_TOPIC);
		}
		this.topic = topic;
		
		if(StringUtils.isBlank(groupId)){
			groupId = consumerProp.getProperty(ConsumerConfig.GROUP_ID_CONFIG);
		}
		this.groupId = groupId;
		
		this.listener = listener;
		logger.debug("初始化kafka消息组类完成");
	}
	
	/**
	 * 启动消费者
	 */
	public void start(){
		startComsummer();
	}

	
	/**
	 * 启动消费者,非自动提交
	 */
	public void startSynCommit(){
		logger.debug("启动非自动提交消费者线程池...");
		this.consumerProp.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		startComsummer();
		logger.debug("启动非自动提交消费者线程池完成");
	}
	
	
	/**
	 * 启动消费者,自动提交
	 */
	public void startAutoCommit(){
		logger.debug("启动自动提交消费者线程池...");
		this.consumerProp.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		startComsummer();
		logger.debug("启动自动提交消费者线程池完成");
	}

	/**
	 * 启动消费者
	 */
	private void startComsummer() {
		logger.debug("启动消费者线程池开始...");
		if(runFlag.compareAndSet(false, true)){
			executorService = Executors.newFixedThreadPool(num);
			for (int i = 0; i < num; i++) {
				KafkaConsumerRunner consumerRunner = new KafkaConsumerRunner(groupId, topic,consumerProp,listener);
				list.add(consumerRunner);
				executorService.submit(consumerRunner);
			}
		}
		logger.debug("启动消费者线程池完成");
	}
	
	
	/**
	 * 关闭kafka消费线程组
	 * @throws Exception
	 */
	public synchronized void shutdown() throws Exception{
		logger.info("开始关闭kafka消费线程组...");
		if(runFlag.compareAndSet(true, false)){
			try {
				if (list != null && list.size() > 0) {
					for (KafkaConsumerRunner consumer : list) {
						try {
							consumer.stop();
						} catch (Exception e) {
							logger.error("关闭kafka消费线程组异常:{}", e.getMessage());
							e.printStackTrace();
						}
					}
				}
				if (executorService != null && !executorService.isShutdown()) {
					executorService.shutdown();
				} 
			} catch (Exception e) {
				logger.error("kafka消费者线程池关闭异常:{}",e.getMessage());
				runFlag.set(true);
				throw e;
			}
		}else{
			logger.error("kafka消费者线程池已关闭");
			throw new Exception("kafka 消费线程类已经关闭");
		}
		logger.info("关闭kafka消费线程组完成");
	}
		
}
