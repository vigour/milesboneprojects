package com.milesbone.queue.kafka.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.kafka.config.KafkaConfiguration;
import com.milesbone.queue.kafka.event.ConsumerEvent;
import com.milesbone.queue.kafka.exception.QueueOperateException;
import com.milesbone.queue.kafka.listener.IKafkaListener;


/**
 * kafka消费者线程类
 * @author miles
 * @date 2017-03-25 下午3:19:39
 */
public class KafkaConsumerRunner implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerRunner.class);
	
	private final AtomicBoolean closed = new AtomicBoolean(false);

	private  Consumer<String, String> consumer;

	private String groupId;

	private String topic;
	
	private IKafkaListener listener;
	
	private Properties consumerProp;

	private Properties kafkatoolsProp;
	
	private boolean autocommit = false;
	
	public KafkaConsumerRunner() {
		this(null,null,null,null);
	}

	public KafkaConsumerRunner(String groupId, String topic) {
		this(groupId, topic,null,null);
	}
	

	/**
	 * @param groupId
	 * @param topic
	 * @param consumerProp
	 */
	public KafkaConsumerRunner(String groupId, String topic, Properties consumerProp) {
		this(groupId, topic,consumerProp,null);
	}

	public KafkaConsumerRunner(String groupId, String topic, Properties consumerProp, IKafkaListener listener) {
		super();
		logger.debug("开始初始化kafka消费者线程类");
		if(consumerProp == null){
			consumerProp = KafkaConfiguration.getInstance().getConsummerProp();
		}
		consumerProp.putAll(consumerProp);
		
		if(StringUtils.isBlank(groupId)){
			groupId = consumerProp.getProperty(ConsumerConfig.GROUP_ID_CONFIG);
		}
		this.groupId = groupId;
		consumerProp.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		
		if(kafkatoolsProp == null){
			kafkatoolsProp = KafkaConfiguration.getInstance().getKafkaProp();
		}
		
		if(StringUtils.isBlank(topic)){
			topic = kafkatoolsProp.getProperty(KafkaConfiguration.CONSUMMER_DEFAULT_TOPIC);
		}
		this.topic = topic;
		
		this.consumerProp = consumerProp;
		this.listener = listener;
		
		this.autocommit = Boolean.parseBoolean(consumerProp.getProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"));
		consumer = new KafkaConsumer<>(consumerProp);
		logger.debug("初始化kafka消费者线程类完成");
	}

	@Override
	public void run() {
		try {
			consumer.subscribe(Arrays.asList(topic));
			final int minBatchSize = Integer.parseInt(kafkatoolsProp.getProperty(KafkaConfiguration.CONSUMMER_BATCH_SIZE, "100"));// 缓存数量
			final boolean batchCallback = Boolean.parseBoolean(kafkatoolsProp.getProperty(KafkaConfiguration.CONSUMMER_BATCH_CALLBACK_MODE, "false"));
			List<ConsumerRecord<String, String>> buffer = new ArrayList<ConsumerRecord<String, String>>();
			while (!closed.get()) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for(TopicPartition partition : records.partitions()){
					List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
					for (ConsumerRecord<String, String> record : partitionRecords) {
						logger.info("消费者接收消息:topic:{},key:{},message:{}",record.topic(),record.key(),record.value());
						if(!batchCallback){
							try {
								if (listener != null) {
									ConsumerEvent event = new ConsumerEvent();
									event.setRecord(record);
									listener.actionPerformed(event);
								}
								if (!autocommit) {
									commitConsumer(Collections.singletonMap(partition, new OffsetAndMetadata(record.offset() + 1)));
								}
							}catch (QueueOperateException e) {
								if (!autocommit) {
									logger.error("发生异常,kafka线程回滚");
									rollback(Collections.singletonMap(partition, new OffsetAndMetadata(record.offset())));
								}
							} catch (Exception e) {
								logger.error("kafka消费者回调异常:{}",e.getMessage());
								e.printStackTrace();
							}
						}else{
							buffer.add(record);
						}
					}
				}
				
				if(batchCallback && buffer.size() > 0){
					try {
						if (listener != null) {
							ConsumerEvent event = new ConsumerEvent();
							event.setRecords(buffer);
							listener.actionPerformed(event);
						}
						if(buffer.size() >= minBatchSize){
							buffer.clear();
						}
	 					if(!autocommit){
	 						commitConsumer();
	 					}
					} catch (Exception e) {
						logger.error("kafka消费者消费消息异常:{}.",e.getMessage());
						throw e;
					}
				}
			}
		} catch (WakeupException e) {
			logger.error("kafka消费者消费消息异常:{}.",e.getMessage());
			if(!closed.get()){
				throw e;
			}
				
		}catch (Exception e) {
			logger.error("kafka消费者消费消息异常:{}.",e.getMessage());
			e.printStackTrace();
		}finally {
			consumer.close();
		}
	}
	
	
	/**
	 * 提交
	 */
	private void commitConsumer() {
		try{
			consumer.commitSync();
		}catch(Exception e){
			consumer.commitSync();
		}		
	}

	/**
	 * 回滚操作
	 * @param toffect
	 */
	private void rollback(Map<TopicPartition, OffsetAndMetadata> toffect) {
		commitConsumer(toffect);
		restart();
	}

	
	/**
	 * 重新启动线程类
	 */
	private void restart() {
		if(consumer != null){
			consumer.close();
			consumer = null;
			consumer = new KafkaConsumer<String, String>(consumerProp);
		}		
	}

	/**
	 * 提交
	 */
	private void commitConsumer(Map<TopicPartition,OffsetAndMetadata> toffect){
		try{
			consumer.commitSync(toffect);
		}catch(Exception e){
			consumer.commitSync(toffect);
		}
	}
	
	
	
	/**
	 * 停止consumer
	 * @throws Exception
	 */
	public void stop() throws Exception{
		closed.set(false);
        consumer.wakeup();
	}
	public void shutdown(){
		closed.set(true);
		consumer.wakeup();
	}
	

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

}
