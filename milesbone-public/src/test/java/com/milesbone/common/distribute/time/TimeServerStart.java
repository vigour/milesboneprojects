package com.milesbone.common.distribute.time;

import java.io.IOException;

import com.milesbone.common.server.impl.TimeServer;

/**
 * 时间服务器启动
 * 
 * @author miles
 * @date 2017-05-02 上午12:36:42
 */
public class TimeServerStart {

	public static void main(String[] args) throws IOException {
		TimeServer thread = new TimeServer();

			thread.start(19999);
	}
}
