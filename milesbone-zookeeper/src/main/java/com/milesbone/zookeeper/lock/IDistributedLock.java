package com.milesbone.zookeeper.lock;

import com.milesbone.zookeeper.listener.IZookeeperListener;


/**
 * 分布式锁接口
 * @author miles
 * @date 2017-03-11 下午10:55:41
 */
public interface IDistributedLock {
	/**
	 * 获取锁 阻塞
	 * @throws Exception
	 */
	public boolean getBlockLock() throws Exception;
	
	/**
	 * 获取锁 非阻塞 调用默认回调
	 * @param lock
	 * @throws Exception
	 */
	public void acquire() throws Exception;
	
	/**
	 * 获取锁 非阻塞 调用指定回调
	 * @param listener
	 * @throws Exception
	 */
	public void getLock(IZookeeperListener listener) throws Exception;
	
	
	/**
	 * 添加监听 
	 * @param listener
	 * @throws Exception
	 */
	public void addListener(IZookeeperListener listener) throws IllegalArgumentException;
	
	/**
	 * 删除监听
	 * @throws Exception
	 */
	public void removeListener();
	
	/**
	 * 替换监听
	 * @param listener
	 * @throws Exception
	 */
	public void changeListener(IZookeeperListener listener) throws IllegalArgumentException;
	/**
	 * 解锁
	 * @throws Exception
	 */
	public void release() throws Exception;

}
