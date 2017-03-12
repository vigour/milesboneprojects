package com.milesbone.zookeeper.util;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.zookeeper.config.ZookeeperConfiguration;

/**
 * 使用默认zookeeper client客户端
 * 
 * @author miles
 * @date 2017-03-11 下午3:20:43
 */
public class ZookeeperClientUtil implements Watcher, ZookeeperUtil {

	private static final Logger logger = LoggerFactory.getLogger(ZookeeperClientUtil.class);

	private ZooKeeper zk = null;
	private CountDownLatch connectedSemaphore = null;
	private String connectionString;
	private int sessionTimeout;
	private Properties zookeeperProp;
	private String systemName;

	public ZookeeperClientUtil() {
		init();
	}

	public ZookeeperClientUtil(String connectionString, int sessionTimeout) {
		super();
		this.connectionString = connectionString;
		this.sessionTimeout = sessionTimeout;
		init();
	}

	public ZookeeperClientUtil(int sessionTimeout) {
		super();
		this.sessionTimeout = sessionTimeout;
		init();
	}

	public ZookeeperClientUtil(String connectionString) {
		super();
		this.connectionString = connectionString;
	}

	/**
	 * 初始化Zookeeper
	 */
	private void init() {
		logger.debug("初始化ZookeeperClientUtil类方法开始");
		zookeeperProp = null;

		zookeeperProp = ZookeeperConfiguration.getInstance().getZookeeperProp();

		connectedSemaphore = null;

		connectedSemaphore = new CountDownLatch(1);

		systemName = zookeeperProp.getProperty("zookeeper.system.name");

		connectionString = "".equals(systemName) ? zookeeperProp.getProperty("zookeeper.servers")
				: (zookeeperProp.getProperty("zookeeper.servers") + "/" + systemName);

		sessionTimeout = Integer.parseInt(zookeeperProp.getProperty("zookeeper.session.timeout"));
		logger.debug("初始化ZookeeperClientUtil类方法完成");
	}

	/**
	 * 创建zookeeper 连接
	 */
	public void create() {
		logger.debug("创建zookeeper连接......");
		try {
			zk = new ZooKeeper(connectionString, sessionTimeout, this);
			connectedSemaphore.await();
		} catch (IOException e) {
			logger.error("创建zookeeper连接异常:{}", e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.error("创建zookeeper线程处理发生异常:{}", e.getMessage());
			e.printStackTrace();
		}
		logger.debug("创建zookeeper 完成");
	}

	/**
	 * 重连zookeeper
	 */
	private void reconnect() {
		try {
			logger.debug("尝试重连zookeeper ......");
			release();
			create();
			logger.debug("重连zookeeper 完成");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * 释放zookeeper 连接
	 */
	public void release() {
		logger.debug("释放zookeeper连接....");
		if (zk != null) {
			try {
				zk.close();
				zk = null;
			} catch (InterruptedException e) {
				logger.error("释放zookeeper连接异常:" + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.debug("释放zookeeper连接完成");
	}

	@Override
	public void process(WatchedEvent event) {
		KeeperState keeperState = event.getState();
		// 事件类型
		EventType eventType = event.getType();
		if (KeeperState.SyncConnected == keeperState) {
			if (EventType.None == eventType) {
				logger.info("连接zookeeper 成功......");
				connectedSemaphore.countDown();
			}
		} else if (KeeperState.Disconnected == keeperState) {
			logger.error("与zookeeper 断开连接");
			reconnect();
		} else if (KeeperState.AuthFailed == keeperState) {
			logger.error("zookeeper 授权失败");
		} else if (KeeperState.Expired == keeperState) {
			logger.error("zookeeper session 过期");
			reconnect();
		}

	}

	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#createNode(java.lang.String, java.lang.String, java.util.List, org.apache.zookeeper.CreateMode)
	 */
	@Override
	public String createNode(String path,String data,List<ACL> acl,CreateMode createMode) throws KeeperException, InterruptedException{
		return zk.create(path, data.getBytes(), acl, createMode);
	}
	
	
	/**
	 * 判断Zookeeper节点是否存在
	 * @param path
	 * @param watch
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public Stat existNode(String path, Watcher watch) throws KeeperException, InterruptedException{
		return zk.exists(path, watch);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#existNode(java.lang.String, boolean)
	 */
	public Stat existNode(String path, boolean watch) throws KeeperException, InterruptedException{
		return zk.exists(path, watch);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#deleteNode(java.lang.String, int)
	 */
	public void deleteNode(String path, int version) throws InterruptedException, KeeperException{
		logger.debug("删除节点{}开始...",path);
		zk.delete(path, version);
		logger.debug("删除节点{}完成",path);
	}
	
	
	public void deleteNodeCascade(String path, int version) throws InterruptedException, KeeperException {
		logger.debug("删除节点{}开始...",path);
		List<String> children = zk.getChildren(path, false);
		String childpath;
		for(String child : children){
			childpath = path + "/" + child;
			deleteNode(childpath, version);
			logger.debug("删除{}下子节点:{}完成",path,childpath);
		}
		deleteNode(path, version);
		logger.debug("删除节点{}完成",path);
	}
	
	/**
	 * 获取zookeeper子节点
	 * @param path
	 * @param watch
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public List<String> getChildren(String path, Watcher watch) throws KeeperException, InterruptedException{
		return zk.getChildren(path, watch);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#getChildren(java.lang.String, java.lang.Boolean)
	 */
	public List<String> getChildren(String path, Boolean watcher) throws KeeperException, InterruptedException{
		return zk.getChildren(path, watcher);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#getData(java.lang.String, org.apache.zookeeper.Watcher, org.apache.zookeeper.data.Stat)
	 */
	@Override
	public byte[] getData(String path, Watcher watch, Stat stat) throws KeeperException, InterruptedException{
		return zk.getData(path, watch, stat);
	}
	
	
	/* (non-Javadoc)
	 * @see com.milesbone.zookeeper.util.ZookeeperUtil#getData(java.lang.String, boolean, org.apache.zookeeper.data.Stat)
	 */
	public byte[] getData(String path, boolean watcher, Stat stat) throws KeeperException, InterruptedException{
		return zk.getData(path, watcher, stat);
	}
	
	/**
	 * 同步
	 * @param path
	 * @param cb
	 * @param ctx
	 */
	public void sync(String path,VoidCallback cb, Object ctx){
		zk.sync(path, cb, ctx);
	}

	
	public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
		Stat stat = zk.setData(path, data, version);
		return stat;
	}
	
	public void close() {
		release();
	}

	@Override
	public Stat setData(String path, byte[] data) throws KeeperException, InterruptedException {
		Stat stat = zk.exists(path, false);
		stat = zk.setData(path, data, stat.getVersion());
		return stat;
	}


}
