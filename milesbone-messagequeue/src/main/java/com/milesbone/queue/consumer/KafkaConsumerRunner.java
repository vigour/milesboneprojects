package com.milesbone.queue.consumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import com.milesbone.queue.config.Configuration;

public class KafkaConsumerRunner implements Runnable {

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final Consumer<String, String> consumer;

	private String groupId;

	private String topic;
	
	public KafkaConsumerRunner() {
		Properties prop = Configuration.getInstance().getConsummerProp();
		consumer = new KafkaConsumer<>(prop);
	}

	public KafkaConsumerRunner(String groupId, String topic) {
		Properties prop = Configuration.getInstance().getConsummerProp();
		prop.put("group.id", groupId);
		consumer = new KafkaConsumer<>(prop);
		this.groupId = groupId;
		this.topic = topic;
	}

	@Override
	public void run() {
		try {
			consumer.subscribe(Arrays.asList(topic));
			while (!closed.get()) {
				ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				
				// Handle new records
			}
		} catch (WakeupException e) {
			if(!closed.get()){
				throw e;
			}
				
		}finally {
			consumer.close();
		}
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
