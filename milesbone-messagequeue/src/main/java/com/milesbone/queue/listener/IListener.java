package com.milesbone.queue.listener;

import com.milesbone.queue.entity.Event;

/**
 * 分布式服务监听回调
 * @author miles
 * @date 2016-12-04 下午8:14:58
 */
public interface IListener {

	/**
	 * 服务变化 回调
	 * @param event
	 * @throws Exception
	 */
	public void actionPerformed(Event event) throws Exception;
}
