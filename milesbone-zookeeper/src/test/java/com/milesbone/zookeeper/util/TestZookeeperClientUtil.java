package com.milesbone.zookeeper.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class TestZookeeperClientUtil extends TestZookeeperUtil{

	
	protected void setUp() throws Exception {
		super.setUp();
		zookeeperUtil = new  ZookeeperClientUtil();
		zookeeperUtil.create();
	}
	
	@Test
	public void testCreateConnection() throws InterruptedException{
		Thread.sleep(1000);		
	}
	
	@Test
	public void testCreateNode() throws InterruptedException, KeeperException{
		zookeeperUtil.createNode("/zkclientPath/t1", "/zkclientPath/t1", Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		Thread.sleep(1000);		
	}
	
	
	@Test
	public void testDeleteNode() throws InterruptedException, KeeperException{
		zookeeperUtil.deleteNode("/zkclientPath/t1", -1);
	}
	
	@Test
	public void testDeleteNodeCascade() throws InterruptedException, KeeperException{
		zookeeperUtil.deleteNodeCascade("/zkclientPath/t1", -1);
	}
	
	@Test
	public void testGetChildren() throws KeeperException, InterruptedException{
		zookeeperUtil.getChildren("/zkclientPath/t1", null);
	}
	
	
	@Test
	public void testgetData() throws KeeperException, InterruptedException{
		Stat stat = new Stat();
		byte[] data = zookeeperUtil.getData("/zkclientPath/t1", null, stat);
		logger.info("获取节点{}数据:{}","/zkclientPath/t1",new String(data));
	}
	
	@Test
	public void testsetData() throws KeeperException, InterruptedException{
		Stat stat = zookeeperUtil.existNode("/zkclientPath/t1", null);
		stat = zookeeperUtil.setData("/zkclientPath/t1", "t1datatest".getBytes(), stat.getVersion());
		logger.info("更新节点{}数据完成,stat:{}","/zkclientPath/t1",stat.getVersion());
	}
}
