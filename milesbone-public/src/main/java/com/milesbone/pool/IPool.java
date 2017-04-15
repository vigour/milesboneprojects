package com.milesbone.pool;


/**
 * 线程池接口
 * @author miles
 * @date 2016-08-24 下午10:49:00
 */
public interface IPool {

	/**
	 * 任务放到线程池处理
	 * @param worker
	 */
	public void execute(Runnable worker);
}
