package com.milesbone.queue.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.milesbone.queue.config.Configuration;
import com.milesbone.queue.exception.QueueOperateException;

/**
 * 消息生产者
 * @author miles
 * @date 2016-08-23 上午11:14:35
 */
public class MessageProducer {

	private Producer<String, String> producer;
	
	public MessageProducer() {
		Properties props = Configuration.getInstance().getProducerProp();

		producer = new KafkaProducer<String, String>(props);
	}

	
	/**
	 * 生产一个消息
	 * @param message
	 * @param topic
	 * @throws QueueOperateException
	 */
	public void sendMessage(String topic ,String message) throws QueueOperateException{
		if(topic == null || "".equals(topic)){
			throw new QueueOperateException("向kafka中生产消息错误:主题不能为空");
		}
		if(message == null || "".equals(message)){
			throw new QueueOperateException("向kafka中生产消息错误:消息不能为空");
		}
		
		try{
			ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message);
			producer.send(record);
			
		}catch (Exception e) {
			throw new QueueOperateException("向kafka中生产消息错误",e);
		}finally {
			if (producer == null) {
				producer.close();
			}
		}
	}
	
}
