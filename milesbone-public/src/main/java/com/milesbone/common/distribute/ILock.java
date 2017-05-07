package com.milesbone.common.distribute;

import java.util.concurrent.TimeUnit;

/**
 * 代表持有资源的对象, 例如
 * 基于jedis的锁自然持有与redis server的连接 </li> 
 * <li> 基于时间统一的的锁自然持有与time server的连接</li> 
 * </ul> 
 * 因此锁应该实现该接口, 并在{@link IReleasable#resease() release} 方法中释放相关的连接
 * @author miles
 * @date 2017-05-01 下午2:27:26
 */
public interface ILock extends IReleasable{

	
	/**
	 * 阻塞性的获取锁, 不响应中断
	 */
	void lock();
	


	/**
	 * 阻塞性的获取锁, 响应中断
	 * @throws InterruptedException
	 */
	void lockInterruptibly() throws InterruptedException;
	
	
	
	/**
	 * 尝试获取锁, 获取不到立即返回, 不阻塞
	 * @return
	 */
	boolean tryLock();
	
	
	
	/**
	 * 尝试获取锁,超时自动返回的阻塞性的获取锁, 不响应中断 
	 * @param time
	 * @param unit
	 * @return {@code true} 若成功获取到锁, {@code false} 若在指定时间内未获取到锁 
	 */
	boolean tryLock(long time, TimeUnit unit);
	
	
	
	
	/**
	 * 超时自动返回的阻塞性的获取锁, 响应中断
	 * @param time
	 * @param unit
	 * @return {@code true} 若成功获取到锁, {@code false} 若在指定时间内未获取到锁 
	 * @throws InterruptedException
	 */
	boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException;
	
	
	
	/**
	 * 释放锁
	 */
	void unlock();
}
