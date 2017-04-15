package com.milesbone.queue.service;

import java.util.List;

import com.milesbone.queue.entity.QueueEntity;
import com.milesbone.queue.exception.QueueOperateException;
import com.milesbone.queue.listener.IKafkaListener;

/**
 * 队列的操作接口
 * @author miles
 * @date 2016-08-22 下午8:29:10
 */
public interface IQueueOperator<T extends QueueEntity> {

	/**
	 * 向队列中放入一个消息  
	 * @param entity
	 */
	public void queue(T entity) throws QueueOperateException;
	
	
	/**
	 * 向队列中放入一个消息  异步
	 * @param entity
	 * @throws QueueOperateException
	 */
	public void queue(T entity, IKafkaListener listener) throws QueueOperateException;
	
	/**
	 * 多个对象入队
	 * @param entity
	 * @throws QueueOperateException
	 */
	public void queue(List<T> entities) throws QueueOperateException;
	
	
	/**
	 * 根据条件从队列中获取一个消息 非阻塞
	 * @param entity
	 * @return
	 * @throws QueueOperateException
	 */
	public QueueEntity dequeue(T entity) throws QueueOperateException;
	
	/**
	 * 根据条件从队列中获取一个消息阻塞
	 * @param entity
	 * @return
	 * @throws QueueOperateException
	 */
	public QueueEntity dequeueBlock(T entity) throws QueueOperateException;
	
	/**
	 * 获取队列中的消息总量
	 * @param queTopic 队列主题
	 * @return
	 * @throws Exception
	 */
	public long getTaskTotalSize(T  queEntity) throws Exception;

	
	/**
	 * 获取队列中当前活动任务量
	 * @param queEntity
	 * @return
	 * @throws Exception
	 */
	public long getActiveTaskSize(T  queEntity) throws Exception;

}
