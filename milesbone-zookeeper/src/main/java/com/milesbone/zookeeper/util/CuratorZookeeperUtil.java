package com.milesbone.zookeeper.util;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.zookeeper.config.ZookeeperConfiguration;

public class CuratorZookeeperUtil implements ZookeeperUtil{

	private static final Logger logger = LoggerFactory.getLogger(CuratorZookeeperUtil.class);
	
	private CuratorFramework curatorFramework = null;
	
	private Properties zookeeperProp;
	
	private CountDownLatch connectedSemaphore = null;
	
	private String connectionString;
	
	private int sessionTimeout;
	
	private String systemName;
	
	private int baseSleeptime;
	
	private int maxRetriescount;
	
	private int maxSleeptime;
	
	private int connectionTimeout;

	public CuratorZookeeperUtil() {
		init();
	}

	
	public void create() {
		logger.debug("创建CuratorFramework开始");
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleeptime, maxRetriescount,maxSleeptime);
		curatorFramework = CuratorFrameworkFactory.builder()
								.connectString(connectionString)
								.sessionTimeoutMs(sessionTimeout)
								.connectionTimeoutMs(connectionTimeout)
								.retryPolicy(retryPolicy)
								.namespace(systemName)
								.build();
		curatorFramework.start();
		logger.debug("创建CuratorFramework完成");
	}
	
	
	/**
	 * 断开连接
	 */
	public void close(){
		logger.debug("关闭curatorFramework开始");
		if(curatorFramework != null){
			curatorFramework.close();
		}
		logger.debug("关闭curatorFramework完成");
	}

	/**
	 * 初始化
	 */
	private void init() {
		logger.debug("初始化CuratorUtil类开始");
		zookeeperProp = ZookeeperConfiguration.getInstance().getZookeeperProp();
		
		connectedSemaphore = new CountDownLatch(1);
		
		connectionString = zookeeperProp.getProperty("zookeeper.servers","127.0.0.1:2181");

		systemName = zookeeperProp.getProperty("zookeeper.system.name","zookeeperCurator");
		sessionTimeout = Integer.parseInt(zookeeperProp.getProperty("zookeeper.session.timeout", "5000"));
		connectionTimeout = Integer.parseInt(zookeeperProp.getProperty("zookeeper.connection.timeout", "5000"));
		baseSleeptime = Integer.parseInt(zookeeperProp.getProperty("cuartor.basesleeptime", "1000"));
		maxRetriescount = Integer.parseInt(zookeeperProp.getProperty("cuartor.maxretriescount", "5"));
		maxSleeptime = Integer.parseInt(zookeeperProp.getProperty("cuartor.maxsleeptime", "60000"));
		logger.debug("初始化CuratorUtil类完成");
	}
	
	
	
	public String createNode(String path,String data,List<ACL> acl,CreateMode createMode) throws KeeperException, InterruptedException{
		logger.debug("创建节点开始...");
		String despath = null;
		try {
			despath =  curatorFramework.create()
					.creatingParentsIfNeeded().withMode(createMode).withACL(acl).forPath(path);
		} catch (Exception e) {
			logger.error("创建节点异常:{}",e.getMessage());
			e.printStackTrace();
		}
		logger.debug("创建{}节点完成",path);
		return despath;
	}


	public Stat existNode(String path, Watcher watcher) throws KeeperException, InterruptedException {
		Stat stat = null;
		try {
			stat = curatorFramework.checkExists().usingWatcher(watcher).forPath(path);
		} catch (Exception e) {
			logger.debug("检查节点是否存在异常:{}",e.getMessage());
			e.printStackTrace();
		}
		
		logger.debug("检查节点是否存在Stat:{}",stat);
		return stat;
	}


	public void deleteNode(String path, int version) throws InterruptedException, KeeperException {
		logger.debug("删除节点{}开始...",path);
		try {
			curatorFramework.delete().guaranteed().withVersion(version).forPath(path);
		} catch (Exception e) {
			logger.error("删除节点异常:{}",e.getMessage());
			e.printStackTrace();
		}
		logger.debug("删除节点{}完成",path);
	}


	public void deleteNodeCascade(String path, int version) throws InterruptedException, KeeperException {
		logger.debug("删除节点{}开始...",path);
		try {
			curatorFramework.delete().guaranteed().
				deletingChildrenIfNeeded().withVersion(version).forPath(path);
		} catch (Exception e) {
			logger.error("删除节点异常:{}",e.getMessage());
			e.printStackTrace();
		}
		logger.debug("删除节点{}完成",path);
	}
	
	public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
		List<String> children = null;
		try {
			children = curatorFramework.getChildren().usingWatcher(watcher).forPath(path);
		} catch (Exception e) {
			logger.error("获取子节点异常{}",e.getMessage());
			e.printStackTrace();
		}
		logger.debug("获取子节点:{}", children.toString());
		return children;
	}


	public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
		byte[] data = null;
		try {
			data = curatorFramework.getData().storingStatIn(stat).usingWatcher(watcher).forPath(path);
		} catch (Exception e) {
			logger.error("获取数据异常:{}",e.getMessage());
			e.printStackTrace();
		}
		logger.debug("获取节点{}数据:{}",path,new String(data));
		return data;
	}


	public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
		logger.debug("更新节点{}数据开始...", path);
		Stat stat = existNode(path, null);
		try {
			stat = curatorFramework.setData().withVersion(version).forPath(path, data);
		} catch (Exception e) {
			logger.error("更新节点{}数据异常:{}",path,e.getMessage());
			e.printStackTrace();
		}
		logger.debug("更新节点{}数据完成",path);
		return stat;
	}


	public Stat setData(String path, byte[] data) throws KeeperException, InterruptedException {
		logger.debug("更新节点{}数据开始...", path);
		Stat stat = null;
		try {
			stat = curatorFramework.setData().forPath(path, data);
		} catch (Exception e) {
			logger.error("更新节点{}数据异常:{}",path,e.getMessage());
			e.printStackTrace();
		}
		logger.debug("更新节点{}数据完成",path);
		return stat;
	}

	
}
