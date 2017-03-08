package com.milesbone.queue.service;

import java.util.List;

import com.milesbone.queue.entity.QueueEntity;
import com.milesbone.queue.exception.QueueOperateException;

/**
 * 队列的操作接口
 * @author miles
 * @date 2016-08-22 下午8:29:10
 */
public interface IQueueOperator {

	/**
	 * 将一个对象入队
	 * @param queueEntity
	 */
	public void queue(QueueEntity queueEntity) throws QueueOperateException;
	
	/**
	 * 多个对象入队
	 * @param queueEntity
	 * @throws QueueOperateException
	 */
	public void queue(List<QueueEntity> queueEntity) throws QueueOperateException;
	
	
	/**
	 * 根据条件从队列中获取一个消息 非阻塞
	 * @param queueEntity
	 * @return
	 * @throws QueueOperateException
	 */
	public QueueEntity dequeue(QueueEntity queueEntity) throws QueueOperateException;
	
	/**
	 * 根据条件从队列中获取一个消息阻塞
	 * @param queueEntity
	 * @return
	 * @throws QueueOperateException
	 */
	public QueueEntity dequeueBlock(QueueEntity queueEntity) throws QueueOperateException;
}
