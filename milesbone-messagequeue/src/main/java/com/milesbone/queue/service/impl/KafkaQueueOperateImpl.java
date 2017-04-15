package com.milesbone.queue.service.impl;

import java.util.List;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.queue.entity.KafkaQueueEntity;
import com.milesbone.queue.entity.QueueEntity;
import com.milesbone.queue.event.ProducerEvent;
import com.milesbone.queue.exception.QueueOperateException;
import com.milesbone.queue.listener.IKafkaListener;
import com.milesbone.queue.producer.KafkaMessageProducer;
import com.milesbone.queue.service.IQueueOperator;

public class KafkaQueueOperateImpl<T extends KafkaQueueEntity> implements IQueueOperator<T> {

	private static final Logger logger = LoggerFactory.getLogger(KafkaQueueOperateImpl.class);
	
	public void queue(KafkaQueueEntity queueEntity) throws QueueOperateException {
		if(queueEntity == null){
			logger.error("队列生产者消息异常:消息不能为空");
			throw new QueueOperateException("队列生产者消息异常:消息不能为空");
		}
		
		KafkaQueueEntity kafkaQueueEntity = null;
		
		try{
			kafkaQueueEntity = (KafkaQueueEntity) queueEntity;
		}catch(Exception e){
			logger.error("向队列生产消息异常：不能强转成kafka实体");
			throw new QueueOperateException("向队列生产消息异常：不能强转成kafka实体",e);
		}
		
		String message = kafkaQueueEntity.getMessage();
		String topic = kafkaQueueEntity.getTopic();
		
		if(topic == null || "".equals(topic)){
			logger.error("向队列生产消息异常：主题不能为空");
			throw new QueueOperateException("向队列生产消息异常：主题不能为空");
		}
		if(message == null || "".equals(message)){
			logger.error("向队列生产消息异常：消息字段不能为空");
			throw new QueueOperateException("向队列生产消息异常：消息字段不能为空");
		}
		
		logger.debug("开始向队列生产消息...");
		KafkaMessageProducer producer = KafkaMessageProducer.getKafkaMessageProducer();
		producer.queue(message,topic);
		logger.debug("队列生产消息完成");
	}

	public void queue(KafkaQueueEntity entity,final IKafkaListener listener) throws QueueOperateException {
		if(entity == null){
			logger.error("队列生产者消息异常:消息不能为空");
			throw new QueueOperateException("队列生产者消息异常:消息不能为空");
		}
		
		if(listener == null){
			logger.error("队列生产者消息异常:listener不能为空");
			throw new QueueOperateException("队列生产者消息异常:listener不能为空");
		}
		
		String message = entity.getMessage();
		String topic = entity.getTopic();
		
		if(topic == null || "".equals(topic)){
			logger.error("向队列生产消息异常：主题不能为空");
			throw new QueueOperateException("向队列生产消息异常：主题不能为空");
		}
		if(message == null || "".equals(message)){
			logger.error("向队列生产消息异常：消息字段不能为空");
			throw new QueueOperateException("向队列生产消息异常：消息字段不能为空");
		}
		logger.debug("开始向队列生产消息...");
		try {
			final String msg = message;
			KafkaMessageProducer producer = KafkaMessageProducer.getKafkaMessageProducer();
			producer.queueCallBack(message, topic, new Callback() {

				public void onCompletion(RecordMetadata metadata, Exception exception) {
					ProducerEvent event = new ProducerEvent();
					event.setException(exception);
					event.setMessage(msg);
					event.setRecordMetadata(metadata);
					try {
						logger.debug("异步回调处理监听器");
						listener.actionPerformed(event);
					} catch (Exception e) {
						logger.error("异步回调异常:{}", e.getMessage());
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			logger.error("向队列中异步放入一个消息异常 :{}",e.getMessage());
			e.printStackTrace();
			throw new QueueOperateException(e);
		}
		logger.debug("队列生产消息完成");
	}
	
	public void queue(List<T> entities) throws QueueOperateException {
		// TODO Auto-generated method stub
	}

	public QueueEntity dequeue(KafkaQueueEntity queueEntity) throws QueueOperateException {
		// TODO Auto-generated method stub
		return null;
	}

	public QueueEntity dequeueBlock(KafkaQueueEntity queueEntity) throws QueueOperateException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public long getTaskTotalSize(KafkaQueueEntity queEntity) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getActiveTaskSize(KafkaQueueEntity queEntity) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	


}
