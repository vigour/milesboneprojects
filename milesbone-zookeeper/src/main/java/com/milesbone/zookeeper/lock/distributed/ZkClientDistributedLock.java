package com.milesbone.zookeeper.lock.distributed;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.zookeeper.listener.IZookeeperListener;
import com.milesbone.zookeeper.lock.IDistributedLock;
import com.milesbone.zookeeper.util.ZookeeperClientUtil;

/**
 * 不可重入 zookeeper 分布式锁实现
 * 
 * @author miles
 * @date 2017-03-11 下午9:02:53
 */
public class ZkClientDistributedLock implements IDistributedLock {
	private static final Logger logger = LoggerFactory.getLogger(ZkClientDistributedLock.class);
	private String lock;// 锁字符串
	private IZookeeperListener listener;// 获取锁后的回调
	private CountDownLatch countDownLatch = null;
	private ZookeeperClientUtil zkClientUtil = null;
	private final String INNER_LOCK = "/innerlock";
	private final String FAIR_ROOT_PATH = "/distributedlock/fair";
	private final String UNFAIR_ROOT_PATH = "/distributedlock/unfair";
	private volatile boolean fair; // true 公平 false 非公平
	private volatile boolean isLock = false; // true 公平 false 非公平
	private IZookeeperDistributedLock distributedLock;
	private AtomicBoolean runFlg = new AtomicBoolean(false);//运行标示
	
	
	public ZkClientDistributedLock() {
		super();
	}

	
	
	public ZkClientDistributedLock(String lock) {
		this(lock,null,true);
	}

	

	public ZkClientDistributedLock(String lock, boolean fair) {
		this(lock,null,fair);
	}



	/**
	 * 公平分布式锁
	 * @param lock
	 * @param listener
	 */
	public ZkClientDistributedLock(String lock, IZookeeperListener listener) {
		this(lock,listener,true);
	}



	public ZkClientDistributedLock(String lock, IZookeeperListener listener, boolean fair) {
		super();
		checkPath(lock);
		countDownLatch = new CountDownLatch(1);
		this.lock = lock;
		this.listener = listener;
		this.fair = fair;
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		logger.debug("初始化分布式锁开始......");
		zkClientUtil = new ZookeeperClientUtil();
		
		try {
			if (fair) {
				this.distributedLock = new FairDistributedLock();

				if (zkClientUtil.existNode(FAIR_ROOT_PATH, false) == null) {
					try {
						zkClientUtil.createNode(FAIR_ROOT_PATH, FAIR_ROOT_PATH, Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
					} catch (Exception e) {
						if (zkClientUtil.existNode(FAIR_ROOT_PATH, false) == null) {
							zkClientUtil.createNode(FAIR_ROOT_PATH, FAIR_ROOT_PATH, Ids.OPEN_ACL_UNSAFE,
									CreateMode.PERSISTENT);
							
						}

					}
				}
			}else{
				this.distributedLock = new UnfairDistributedLock();
				
				if(zkClientUtil.existNode(UNFAIR_ROOT_PATH, false) == null){
					try {
						zkClientUtil.createNode(UNFAIR_ROOT_PATH, UNFAIR_ROOT_PATH, Ids.OPEN_ACL_UNSAFE,
								CreateMode.PERSISTENT);
					} catch (Exception e) {
						if(zkClientUtil.existNode(UNFAIR_ROOT_PATH, false) == null){
								zkClientUtil.createNode(UNFAIR_ROOT_PATH, UNFAIR_ROOT_PATH, Ids.OPEN_ACL_UNSAFE,
										CreateMode.PERSISTENT);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("初始化分布式锁异常:{}",e.getMessage());
		}
		logger.debug("初始化分布式锁完成");
	}

	private void checkPath(String path) {
		if (StringUtils.isBlank(path)) {
			logger.error("分布式锁路径不能为空");
			throw new IllegalArgumentException("分布式锁路径不能为空");
		}

		if (path.indexOf("/") == -1) {
			logger.error("分布式锁路径必须以/开始");
			throw new IllegalArgumentException("分布式锁路径必须以/开始");
		}
	}

	public boolean getBlockLock() throws Exception {
		startGetLock();
		countDownLatch.await();
		return isLock;
	}

	/**
	 * 开始获取锁
	 * @throws Exception 
	 */
	private void startGetLock() throws Exception {
		if(runFlg.compareAndSet(false, true)){
			Thread t = new Thread(distributedLock);
			t.start();
		}else{
			logger.error("分布式锁已经存在");
			throw new Exception("分布式锁已经存在");
		}
	}



	public void acquire() throws Exception {
		if(listener == null){
			logger.error("分布式锁Listener不能为空!");
			throw new IllegalArgumentException("分布式锁Listener不能为空!");
		}
		
		startGetLock();
		
	}

	public void getLock(IZookeeperListener listener) throws Exception {
		changeListener(listener);
	}

	public void addListener(IZookeeperListener listener) throws IllegalArgumentException {
		if(listener == null){
			logger.error("分布式锁Listener不能为空!");
			throw new IllegalArgumentException("分布式锁Listener不能为空!");
		}
		synchronized (this.listener) {
			this.listener = listener;
		}
		
	}

	public void removeListener() {
		this.listener = null;
	}

	public void changeListener(IZookeeperListener listener) throws IllegalArgumentException {
		addListener(listener);
	}

	public void release() throws Exception {
		try {
			distributedLock.unLock();
		} catch (Exception e) {
			logger.error("分布式锁解锁异常:{}",e.getMessage());
			e.printStackTrace();
		}finally {
			zkClientUtil.release();
		}
	}

	/**
	 * 回调
	 */
	private void callback() {
		try {
			if (listener == null) {
				listener.notifyObservers(null);
			}
		} catch (Exception e) {
			logger.error("zookeeper分布式锁回调异常:{}", e.getMessage());
			e.printStackTrace();
		} finally {
			countDownLatch.countDown();
		}
	}

	/**
	 * 计算当前的路径
	 * 
	 * @param path
	 * @param rootPath
	 * @return
	 */
	private String calculateCurrPath(String path, String rootPath) {
		return path.substring(path.indexOf(rootPath) + rootPath.length() + 1);
	}

	/**
	 * 分布式锁接口
	 * 
	 * @author miles
	 * @date 2017-03-11 下午9:24:19
	 */
	interface IZookeeperDistributedLock extends Watcher, Runnable {

		/**
		 * 锁处理
		 * 
		 * @throws Exception
		 */
		public void procLock() throws Exception;

		/**
		 * 解锁
		 * 
		 * @throws Exception
		 */
		public void unLock() throws Exception;
	}

	class UnfairDistributedLock implements IZookeeperDistributedLock {
		private String unfairLockPath;

		public UnfairDistributedLock() {
			super();
		}

		public void process(WatchedEvent event) {

			KeeperState state = event.getState();

			EventType type = event.getType();

			if (KeeperState.SyncConnected == state) {
				if (EventType.NodeDeleted == type) {
					try {
						procLock();
					} catch (Exception e) {
						logger.error("处理非公平锁异常:{}", e.getMessage());
						;
						e.printStackTrace();
					}
				}
			} else {
				logger.error("zookeeper非公平锁处理失败");
			}
		}

		public void run() {
			try {
				procLock();
			} catch (Exception e) {
				logger.error("zookeeper非公平锁线程异常:{}", e.getMessage());
				e.printStackTrace();
			}
		}

		public void procLock() throws Exception {
			logger.debug("开始处理非公平锁....");
			unfairLockPath = UNFAIR_ROOT_PATH + lock;

			try {
				zkClientUtil.sync(unfairLockPath, new VoidCallback() {

					public void processResult(int rc, String path, Object ctx) {
					}
				}, zkClientUtil);

				if (zkClientUtil.existNode(unfairLockPath, false) == null) {
					try {
						zkClientUtil.createNode(unfairLockPath, unfairLockPath, Ids.OPEN_ACL_UNSAFE,
								CreateMode.EPHEMERAL);
						isLock = true;
						callback();
					} catch (Exception e) {
						zkClientUtil.getData(unfairLockPath, this, new Stat());
					}
				} else {
					zkClientUtil.getData(unfairLockPath, this, new Stat());
				}
			} catch (Exception e) {
				logger.error("zookeeper 分布式非公平锁获取异常:{}", e.getMessage());
				e.printStackTrace();
				countDownLatch.countDown();
			}
			logger.debug("非公平锁处理完成");
		}

		public void unLock() throws Exception {
			zkClientUtil.deleteNode(unfairLockPath, -1);
		}

	}

	/**
	 * 公平分布式锁
	 * 
	 * @author miles
	 * @date 2017-03-11 下午9:46:19
	 */
	class FairDistributedLock implements IZookeeperDistributedLock {
		private String waitPath;
		private String currentPath;
		private String fairLockPath;

		
		
		public FairDistributedLock() {
			super();
		}

		@Override
		public void process(WatchedEvent event) {
			KeeperState state = event.getState();

			EventType type = event.getType();

			if (KeeperState.SyncConnected == state) {
				if (EventType.NodeDeleted == type) {
					try {
						zkClientUtil.sync(fairLockPath, new VoidCallback() {

							public void processResult(int rc, String path, Object ctx) {
							}
						}, zkClientUtil);

						List<String> children = zkClientUtil.getChildren(fairLockPath, false);

						childrenProc(children);
					} catch (Exception e) {
						logger.error("zookeeper公平分布式锁执行异常:{}", e.getMessage());
						e.printStackTrace();
						countDownLatch.countDown();
					}
				}
			}

		}

		public void run() {
			try {
				procLock();
			} catch (Exception e) {
				logger.error("执行公平分布式锁线程异常:{}", e.getMessage());
				e.printStackTrace();
			}
		}

		public void procLock() throws Exception {
			String path;
			fairLockPath = FAIR_ROOT_PATH + lock;

			logger.debug("开始获取公平分布式锁......");
			try {
				if (zkClientUtil.existNode(fairLockPath, false) == null) {
					try {
						zkClientUtil.createNode(fairLockPath, fairLockPath, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					} catch (Exception e) {
						if (zkClientUtil.existNode(fairLockPath, false) == null) {
							zkClientUtil.createNode(fairLockPath, fairLockPath, Ids.OPEN_ACL_UNSAFE,
									CreateMode.PERSISTENT);
						}

					}
				}
				path = zkClientUtil.createNode(fairLockPath + INNER_LOCK, INNER_LOCK, Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL_SEQUENTIAL);
				currentPath = calculateCurrPath(path, fairLockPath);
				zkClientUtil.sync(fairLockPath, new VoidCallback() {

					public void processResult(int rc, String path, Object ctx) {

					}
				}, zkClientUtil);
				List<String> children = zkClientUtil.getChildren(fairLockPath, false);
				childrenProc(children);
			} catch (Exception e) {
				logger.error("zookeeper公平分布式锁执行异常:{}", e.getMessage());
				countDownLatch.countDown();
				e.printStackTrace();
			}

			logger.debug("获取公平分布式锁完成");
		}

		public void unLock() throws Exception {
			zkClientUtil.deleteNode(fairLockPath + "/" + currentPath, -1);

			List<String> children = zkClientUtil.getChildren(fairLockPath, false);
			if (children != null && children.size() > 0) {
				return;
			}
			zkClientUtil.deleteNode(fairLockPath, -1);
		}

		/**
		 * 分析处理子节点
		 * 
		 * @param children
		 */
		private void childrenProc(List<String> children) {
			logger.debug("开始分析子节点");

			try {
				if (children == null || children.size() == 0) {
					logger.error("zookeeper 分布式锁主路径下的子节点,无法找到子节点!");
					throw new IllegalArgumentException("zookeeper 选举主路径下的子节点,无法找到子节点!");
				}
				if (children.size() == 1) {
					isLock = true;
					callback();
				} else {
					Collections.sort(children);
					logger.info("子节点开始排序 :{}", children.toString());

					int index = children.indexOf(currentPath);
					logger.info("子节点开始排序 :{}, index:{}", children.toString(), index);

					switch (index) {
					case -1:
						logger.error("zookeeper公平分布式锁异常:路径未找到");
						break;
					case 0:
						isLock = true;
						callback();
						break;
					default:
						waitPath = fairLockPath + "/" + children.get(index - 1);
						zkClientUtil.getData(waitPath, this, new Stat());
						break;
					}
				}
			} catch (Exception e) {
				logger.error("zookeeper公平分布式锁处理异常:{}", e.getMessage());
				e.printStackTrace();
			}
			logger.debug("分析子节点完成");
		}

	}

}
