package com.milesbone.common.distribute.lock;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.distribute.ILock;


/**
 * 锁的抽象实现, 真正的获取锁的步骤由子类去实现
 * @author miles
 * @date 2017-05-01 下午3:00:14
 */
public abstract class AbstractLock implements ILock {

	private static final Logger logger = LoggerFactory.getLogger(AbstractLock.class);
	
	/**
	 * 这里需不需要保证可见性值得讨论, 因为是分布式的锁,  
	 * 1.同一个jvm的多个线程使用不同的锁对象其实也是可以的, 这种情况下不需要保证可见性  
	 * 2.同一个jvm的多个线程使用同一个锁对象, 那可见性就必须要保证了. 
	 */
	protected volatile boolean locked;
	
	/**
	 * 当前jvm内持有该锁的线程(if have one)
	 */
	private Thread exclusiveOwnerThread;
	

	public void lock() {
		try {
			lock(false, 0, null, false);
		} catch (InterruptedException e) {
			logger.error("获取分布式锁失败{}",e);
			e.printStackTrace();
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		lock(false, 0, null, true);
	}


	public boolean tryLock(long time, TimeUnit unit) {
		try {
			return lock(true, 0, null, false);
		} catch (InterruptedException e) {
			logger.error("获取分布式锁失败{}",e);
			e.printStackTrace();
		}
		return false;
	}

	public boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException {
		try {
			return lock(true, time, unit, true);
		} catch (InterruptedException e) {
			logger.error("获取分布式锁失败{}",e);
			e.printStackTrace();
		}
		return false;
	}

	public void unlock() {
		logger.debug("释放锁操作开始...");
		if(Thread.currentThread() != getExclusiveOwnerThread()){
			logger.error("当前线程未持有锁{}",Thread.currentThread().getName());
			throw new IllegalMonitorStateException("current thread does not hold the lock");
		}
		
		releaseLock();//释放锁
		setExclusiveOwnerThread(null);
		logger.debug("释放锁操作完成");
	}
	
	/**
	 * 释放锁操作实现
	 */
	 protected abstract void releaseLock();

	/** 
     * 阻塞式获取锁的实现 
     *  
     * @param useTimeout  
     * @param time 
     * @param unit 
     * @param interrupt 是否响应中断 
     * @return 
     * @throws InterruptedException 
     */  
    protected abstract boolean lock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException;

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}

	protected void setExclusiveOwnerThread(Thread exclusiveOwnerThread) {
		this.exclusiveOwnerThread = exclusiveOwnerThread;
	}

}
