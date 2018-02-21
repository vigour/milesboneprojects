package com.milesbone.common.distribute.time;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Test;

import com.milesbone.common.clinet.impl.TimeClient;
import com.milesbone.common.test.base.AbstactServerBeanTestCase;

public class TimeServerTest extends AbstactServerBeanTestCase{

	@Resource(name="timeClient")
	private TimeClient client;
	
	@Test
	public void testInitial() throws Exception{
		logger.debug("hello ");
	}
	
	
	
	@Test
	public void testTimeClient() throws IOException{
		try {
			
			System.out.println(client.currentTimeMillis());
			 client.stop();
			System.in.read();
		} catch (Exception e) {
		} 
	}



	@Override
	public void setup() {
		logger.debug("初始化客户端服务...");
//		try {
//			client = new TimeClient();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		logger.debug("客户端服务初始化完成");
		
	}



	@Override
	public void tearDown() {
		try {
			logger.debug("客户端服务开始关闭...");
			client.close();
			logger.debug("客户端服务关闭完成...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
