package com.milesbone.zookeeper.lock.distributed;

import com.milesbone.zookeeper.listener.IZookeeperListener;
import com.milesbone.zookeeper.lock.IDistributedLock;


/**
 * Curator分布式锁
 * @author miles
 * @date 2017-03-26 上午10:23:57
 */
public class CuratorRecipesLock implements IDistributedLock {
	
//	private static final Logger logger = LoggerFactory.getLogger(CuratorRecipesLock.class);
//	
//	private String lock;//分布式锁的节点
//	private IZookeeperListener listener;// 获取锁后的回调
//	private CountDownLatch countDownLatch = null;
//	private CuratorZookeeperUtil curatorZookeeperUtil = null;
//	
//	private final String INNER_LOCK = "/curator_innerlock";
	public CuratorRecipesLock() {
		super();
	}

	public boolean getBlockLock() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void acquire() throws Exception {
		// TODO Auto-generated method stub

	}

	public void getLock(IZookeeperListener listener) throws Exception {
		// TODO Auto-generated method stub

	}

	public void addListener(IZookeeperListener listener) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	public void removeListener() {
		// TODO Auto-generated method stub

	}

	public void changeListener(IZookeeperListener listener) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	public void release() throws Exception {
		// TODO Auto-generated method stub

	}

}
