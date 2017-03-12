package com.milesbone.zookeeper.election.distributed;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.zookeeper.election.LeaderElection;
import com.milesbone.zookeeper.listener.IListener;
import com.milesbone.zookeeper.util.ZookeeperClientUtil;

/**
 * Zookeeper 选举
 * 
 * @author miles
 * @date 2017-03-11 上午10:38:37
 */
public class ZkClientLeaderElection implements LeaderElection, Watcher {

	private Logger logger = LoggerFactory.getLogger(ZkClientLeaderElection.class);
	private static final String LeaderElection_PATH = "/ZkclientLeaderElection/election";// 选举路径
	private static final String LeaderElection_ROOT_PATH = "/ZkclientLeaderElection";// rootPath
	private ZookeeperClientUtil zkClientUtil = null;
	private IListener listener;
	private boolean isNew = true;
	private CountDownLatch countDownLatch = null;
	private AtomicBoolean runFlag = new AtomicBoolean(false);
	private String currentPath;
	private String waitPath;

	@SuppressWarnings("unused")
	private volatile boolean isLeader = false;

	public ZkClientLeaderElection() {
	}

	public ZkClientLeaderElection(IListener listener) {
		if (listener == null) {
			logger.error("Listener can not be null!");
			throw new IllegalArgumentException("Listener can not be null!");
		}

		this.listener = listener;
		countDownLatch = new CountDownLatch(1);

		zkClientUtil = new ZookeeperClientUtil();
		logger.debug("生成rootpath");
		if (isNew) {
			try {
				if (zkClientUtil.existNode(LeaderElection_ROOT_PATH, false) == null) {
					zkClientUtil.createNode(LeaderElection_ROOT_PATH, LeaderElection_ROOT_PATH, Ids.OPEN_ACL_UNSAFE,
							CreateMode.PERSISTENT);
				}
			} catch (KeeperException | InterruptedException e) {
				logger.error("创建zookeeper 选举节点失败");
				e.printStackTrace();
			}
		}
		isNew = false;
	}

	public void leaderElectionStart() throws Exception {
		logger.debug("开启选举线程......");
		if (!runFlag.compareAndSet(false, true)) {
			LeaderElectionThread let = new LeaderElectionThread();
			let.start();
		} else {
			logger.error("Election is running......");
			throw new Exception("Election is running......");
		}
		logger.debug("选举线程启动完成");
	}

	private void callBack() {
		try {
			isLeader = true;
			listener.actionPerformed(null);
		} catch (Exception e) {
			logger.error("zookeeper选举回调异常:{}", e);
			e.printStackTrace();
		}
	}
	

	public void process(WatchedEvent event) {
		KeeperState sta = event.getState();

		EventType type = event.getType();
		if (KeeperState.SyncConnected == sta) {

			if (EventType.NodeDeleted == type) {
				try {

					zkClientUtil.sync(LeaderElection_ROOT_PATH, new VoidCallback() {

						public void processResult(int arg0, String arg1, Object arg2) {
						}
					}, zkClientUtil);

					List<String> childRen = zkClientUtil.getChildren(LeaderElection_ROOT_PATH, false);

					procChildren(childRen);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.error("zookeeper operate error :{}", event);
		}

	}

	/**
	 * 分析处理子节点
	 * 
	 * @param children
	 */
	private void procChildren(List<String> children) {
		logger.debug("开始分析子节点");

		if (children == null || children.size() == 0) {
			logger.error("zookeeper 选举主路径下的子节点,无法找到子节点!");
			throw new IllegalArgumentException("zookeeper 选举主路径下的子节点,无法找到子节点!");
		}

		try {
			if (children.size() == 1) {
				callBack();
			} else {
				Collections.sort(children);

				int index = children.indexOf(currentPath);
				logger.info("子节点开始排序 :{}, index:{}", children.toString(),index);

				switch (index) {
				case -1:
					logger.error("zookeeper选举异常:路径未找到");
					break;
				case 0:
					callBack();
					break;
				default:
					waitPath = LeaderElection_ROOT_PATH + "/" + children.get(index - 1);
					zkClientUtil.getData(waitPath, this, new Stat());
				}
			}
		} catch (KeeperException | InterruptedException e) {
			logger.error("分析子节点异常:{}", e.getMessage());
			e.printStackTrace();
		}
		logger.debug("分析子节点完成");
	}

	/**
	 * 截取当前路径
	 * 
	 * @param path
	 * @return
	 */
	private String calculateCurrPath(String path) {
		return path.substring(path.indexOf(LeaderElection_ROOT_PATH + LeaderElection_ROOT_PATH.length() + 1));
	}

	public class LeaderElectionThread extends Thread {

		public LeaderElectionThread() {

		}

		public void run() {
			logger.debug("开始选举......");

			String path;
			try {

				path = zkClientUtil.createNode(LeaderElection_PATH, LeaderElection_PATH, Ids.OPEN_ACL_UNSAFE,
						CreateMode.EPHEMERAL_SEQUENTIAL);

				currentPath = calculateCurrPath(path);

				zkClientUtil.sync(LeaderElection_ROOT_PATH, new VoidCallback() {

					public void processResult(int rc, String path, Object ctx) {
					}
				}, zkClientUtil);

				logger.debug("获取所有子节点");
				List<String> children = zkClientUtil.getChildren(LeaderElection_ROOT_PATH, false);

				logger.debug("分析处理子节点...");
				procChildren(children);

				logger.debug("若当前节点不是leader 则等待");
				countDownLatch.await();
				Thread.sleep(1 * 1000);

			} catch (KeeperException e) {
				logger.error("zookeeper leader 选举异常:{}", e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				logger.error("zookeeper leader 选举线程中断异常:{}", e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
