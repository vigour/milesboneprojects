package com.milesbone.zookeeper.util;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.zookeeper.config.ZookeeperConfiguration;

public class ZookeeperClientFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(ZookeeperClientFactory.class);
	
	private Properties zookeeperProp;
	private static String connectString;
	private static int sessionTimeout;
	private String systemName = null;
	private static final ZookeeperClientFactory instace = new  ZookeeperClientFactory();
	private ZookeeperClientFactory(){
		init();
	}
	
	
	private void init() {
		zookeeperProp = ZookeeperConfiguration.getInstance().getZookeeperProp();
		systemName = zookeeperProp.getProperty("zookeeper.system.name");
		connectString = "".equals(systemName) ? zookeeperProp.getProperty("zookeeper.servers") : (zookeeperProp.getProperty("zookeeper.servers") + "/" + systemName);
		sessionTimeout = Integer.parseInt(zookeeperProp.getProperty("zookeeper.session.timeout"));
	}


	public static ZookeeperClientFactory getInstance(){
		return instace;
	}
	public static ZooKeeper create() {
		
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		ZooKeeper zkClient = null;
		try {
			zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				
				@Override
				public void process(WatchedEvent event) {
					logger.trace("conn Event path:" + event.getPath());
					logger.trace("Event stat:" + event.getState());
					logger.trace("Event type:" + event.getType());
					
					if(KeeperState.SyncConnected == event.getState()){
						logger.debug("connect success!");
						countDownLatch.countDown();
					}
				}
			});
			logger.debug("zkcleint stat:" + zkClient.getState());
		} catch (IOException e) {
			logger.error("创建zookeeper 连接出现异常:" + e.getMessage());
			e.printStackTrace();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.error("线程处理出现异常" + e.getMessage());
			e.printStackTrace();
		}
		
		return zkClient;
	}

}
