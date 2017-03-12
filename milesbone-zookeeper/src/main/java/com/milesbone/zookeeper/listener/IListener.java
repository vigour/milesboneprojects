package com.milesbone.zookeeper.listener;

import com.milesbone.zookeeper.event.Event;

/**
 * 分布式服务监听回调
 * @author miles
 * @date 2017-03-11 上午10:41:37
 */
public interface IListener {

	
	public void actionPerformed(Event event) throws Exception;
}
