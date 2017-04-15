package com.milesbone.zookeeper.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class TestCuratorFrameworkUtil extends TestZookeeperUtil{

	protected void setUp() throws Exception {
		super.setUp();
		zookeeperUtil = new CuratorZookeeperUtil();
		zookeeperUtil.create();
	}
	
	
	
	@Test
	public void testCreate(){
	}
	
	@Test
	public void testCreateNode() throws KeeperException, InterruptedException{
		zookeeperUtil.createNode("/curatorzkPath/t1/t3","curatorzkPath",Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
	}
	
	@Test
	public void testCreateNodeAsync() throws KeeperException, InterruptedException{
		zookeeperUtil.createNodeAsync("/curatorzkPath/t2/t1","curatorzkPath1",Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
	}
	
	@Test
	public void testexistNode() throws KeeperException, InterruptedException{
		zookeeperUtil.existNode("/curatorzkPath/t1/t2",null);
	}
	
	@Test
	public void tesdeleteNode() throws KeeperException, InterruptedException{
		zookeeperUtil.deleteNode("/curatorzkPath/t1",-1);
	}
	
	@Test
	public void tesdeleteNodeCascade() throws KeeperException, InterruptedException{
		zookeeperUtil.deleteNodeCascade("/curatorzkPath/t1",-1);
	}
	
	@Test
	public void testGetChildren() throws KeeperException, InterruptedException{
		zookeeperUtil.getChildren("/curatorzkPath/t1", null);
	}
	
	@Test
	public void testgetData() throws KeeperException, InterruptedException{
		Stat stat = new Stat();
		zookeeperUtil.getData("/curatorzkPath/t1", null, stat);
	}
	
	@Test
	public void testsetData() throws KeeperException, InterruptedException{
		Stat stat = new Stat();
//		zookeeperUtil.setData("/curatorzkPath/t1", "t1databegin".getBytes(), 1);
		zookeeperUtil.setData("/curatorzkPath/t1", "t1datacar".getBytes());
		zookeeperUtil.getData("/curatorzkPath/t1", null, stat);
	}
	
}
