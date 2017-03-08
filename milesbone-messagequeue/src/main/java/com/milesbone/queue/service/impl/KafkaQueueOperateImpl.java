package com.milesbone.queue.service.impl;

import java.util.List;

import com.milesbone.queue.entity.KafkaQUeueEntity;
import com.milesbone.queue.entity.QueueEntity;
import com.milesbone.queue.exception.QueueOperateException;
import com.milesbone.queue.producer.MessageProducer;
import com.milesbone.queue.service.IQueueOperator;

public class KafkaQueueOperateImpl implements IQueueOperator {

	public void queue(QueueEntity queueEntity) throws QueueOperateException {
		if(queueEntity == null){
			throw new QueueOperateException("队列生产者消息异常:消息不能为空");
		}
		
		KafkaQUeueEntity kafkaQUeueEntity = null;
		
		try{
			kafkaQUeueEntity = (KafkaQUeueEntity) queueEntity;
		}catch(Exception e){
			throw new QueueOperateException("向队列生产消息异常：不能强转成kafka实体",e);
		}
		
		String message = kafkaQUeueEntity.getMessage();
		String topic = kafkaQUeueEntity.getTopic();
		
		if(topic == null || "".equals(topic)){
			throw new QueueOperateException("向队列生产消息异常：主题不能为空");
		}
		if(message == null || "".equals(message)){
			throw new QueueOperateException("向队列生产消息异常：消息字段不能为空");
		}
		
		MessageProducer producer = new MessageProducer();
		producer.sendMessage(topic, message);
	}

	public void queue(List<QueueEntity> queueEntity) throws QueueOperateException {
		// TODO Auto-generated method stub
	}

	public QueueEntity dequeue(QueueEntity queueEntity) throws QueueOperateException {
		// TODO Auto-generated method stub
		return null;
	}

	public QueueEntity dequeueBlock(QueueEntity queueEntity) throws QueueOperateException {
		// TODO Auto-generated method stub
		return null;
	}

}
