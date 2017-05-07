package com.milesbone.common.clinet.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.clinet.IClient;
import com.milesbone.common.config.TimeServerConfiguration;
import com.milesbone.common.config.TimeServerConfiguration.Configuration;
import com.milesbone.common.exception.TimeClientException;

/**
 * 时间服务器客户端
 * 
 * @author miles
 * @date 2017-05-01 下午11:31:23
 */
public class TimeClient implements IClient {

	private static final Logger logger = LoggerFactory.getLogger(TimeClient.class);

	private static final String TIME_CMD = "time";

	private final SocketAddress address;

	private SocketChannel channel;

	/**
	 * @throws IOException
	 * 
	 */
	public TimeClient() throws IOException {
		this(null);
	}

	/**
	 * 时间服务器客户端构造方法
	 * 
	 * @param address
	 * @throws IOException
	 */
	public TimeClient(SocketAddress address) throws IOException {
		super();
		logger.debug("初始化时间服务器客户端...");
		if (address == null) {
			Properties prop = TimeServerConfiguration.getInstance().getTimeServerProp();
			String host = prop.getProperty(Configuration.DEFAULT_HOST_NAME.getKey(),
					Configuration.DEFAULT_HOST_NAME.getValue());
			int port = Integer.parseInt(
					prop.getProperty(Configuration.DEFAULT_PORT.getKey(), Configuration.DEFAULT_PORT.getValue()));
			address = new InetSocketAddress(host, port);
		}
		this.address = address;
		channel = SocketChannel.open(address);
		channel.configureBlocking(true);// blocking mode
		logger.debug("初始化时间服务器客户端完成");
	}

	/**
	 * 同步当前时间
	 * 
	 * @return
	 */
	public long currentTimeMillis() {
		try {
			channel.write(ByteBuffer.wrap(TIME_CMD.getBytes()));
			ByteBuffer buffer = ByteBuffer.allocate(64);
			channel.read(buffer);
			buffer.flip();// flip for use of read
			byte[] bytes = new byte[buffer.limit() - buffer.position()];
			System.arraycopy(buffer.array(), buffer.position(), bytes, 0, bytes.length);
			return Long.parseLong(new String(bytes));
		} catch (NumberFormatException e) {
			logger.error("格式化结果异常{},返回当前时间", e);
			return System.currentTimeMillis();
		} catch (IOException e) {
			logger.error("处理时间服务器客户端异常{},抛出", e);
			throw new TimeClientException(address);
		}
	}

	/**
	 * 停止时间服务器客户端
	 */
	public void stop() {
		logger.debug("停止时间服务器客户端开始..");
		try {
			if (channel != null) {
				channel.close();
			}
		} catch (Exception e) {
			logger.error("停止时间服务器客户端异常{}", e);
		}
		logger.debug("停止时间服务器客户端完成");
	}

	public void start() throws Exception {
		// TODO Auto-generated method stub

	}

	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void restart() throws Exception {
		// TODO Auto-generated method stub

	}

}
