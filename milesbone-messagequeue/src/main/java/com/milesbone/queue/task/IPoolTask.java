package com.milesbone.queue.task;

import com.milesbone.queue.pool.QueueThreadPool;

/**
 * 线程池任务接口
 * @author miles
 * @date 2016-08-23 下午11:09:45
 * @param <T>
 */
public interface IPoolTask<T> {

	/**
	 * 初始化
	 * @param id
	 * @param pool
	 * @param Param
	 */
	public void init(String id, QueueThreadPool pool, T Param);
	
	/**
	 * 获取线程ID
	 * @return
	 */
	public String getThreadId();
	
	/**
	 * 线程开始时间
	 * @return
	 */
	public long getStartTime();
	
	/**
	 * 关闭线程
	 * @throws Exception
	 */
	public void stop() throws Exception;
	
	
	/**
	 * 获取当前线程
	 * @return
	 */
	public Thread getThread();
	
	
	/**
	 * 获取线程运行标识
	 * @return
	 */
	public boolean getRunFlag();
	
}
