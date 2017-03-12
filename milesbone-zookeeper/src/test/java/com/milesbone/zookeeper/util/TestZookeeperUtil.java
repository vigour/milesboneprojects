package com.milesbone.zookeeper.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class TestZookeeperUtil extends TestCase {
	protected ZookeeperUtil zookeeperUtil = null;
	protected Logger logger = LoggerFactory.getLogger(TestZookeeperUtil.class);
	protected void tearDown() throws Exception {
		super.tearDown();
		zookeeperUtil.close();
	}
}
