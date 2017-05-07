package com.milesbone.common.distribute;


/**
 * 代表持有资源的对象, 例如
 * 基于jedis的锁自然持有与redis server的连接 </li> 
 * <li> 基于时间统一的的锁自然持有与time server的连接</li> 
 * </ul> 
 * 因此锁应该实现该接口, 并在{@link Releasable#resease() release} 方法中释放相关的连接
 * @author miles
 * @date 2017-05-01 上午11:13:03
 */
public interface IReleasable {

	
	/**
	 * 释放所有资源
	 */
	void release();
}
