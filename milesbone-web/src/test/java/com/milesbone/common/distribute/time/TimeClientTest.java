package com.milesbone.common.distribute.time;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.junit.Test;

import com.milesbone.common.clinet.impl.TimeClient;

import junit.framework.TestCase;

public class TimeClientTest extends TestCase{
	
	

	
	@Test
	public void testTimeClient() throws IOException{
		try {
			TimeClient client = new TimeClient();
			System.out.println(client.currentTimeMillis());
			 client.stop();
//			System.in.read();
		} catch (Exception e) {
		} 
	}
	
	@Test
	public void testTimeClientAddress() throws IOException{
		try {
			TimeClient client = new TimeClient(new InetSocketAddress("localhost", 19999));
			System.out.println(client.currentTimeMillis());
			client.stop();
			System.in.read();
		} catch (Exception e) {
		} 
	}
}
