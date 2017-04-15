package com.milesbone.queue.event;

import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * 消费者事件监听
 * @author miles
 * @date 2017-03-25 下午2:22:15
 */
public class ConsumerEvent implements IKafkaEvent {

	/**
	 * 单笔消息
	 */
	private ConsumerRecord<String, String> record = null;
	
	/**
	 * 多笔消息
	 */
	private List<ConsumerRecord<String, String>> records = null;

	/**
	 * 异常
	 */
	private Exception exception = null;

	public ConsumerRecord<String, String> getRecord() {
		return record;
	}

	public void setRecord(ConsumerRecord<String, String> record) {
		this.record = record;
	}

	public List<ConsumerRecord<String, String>> getRecords() {
		return records;
	}

	public void setRecords(List<ConsumerRecord<String, String>> records) {
		this.records = records;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
	
}
