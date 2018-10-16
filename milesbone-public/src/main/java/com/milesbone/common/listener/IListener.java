package com.milesbone.common.listener;

import com.milesbone.common.event.IEvent;

/**
 * 分布式监听器
 * @author miles
 * @date 2017-03-25 下午4:49:11
 */
public interface IListener {

	/**
	 * 通知观察者
	 * @param event
	 * @throws Exception
	 */
	public void notifyObservers(IEvent event) throws Exception;
}
