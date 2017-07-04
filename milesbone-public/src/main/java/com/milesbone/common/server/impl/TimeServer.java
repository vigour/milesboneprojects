package com.milesbone.common.server.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.common.config.TimeServerConfiguration;
import com.milesbone.common.config.TimeServerConfiguration.Configuration;
import com.milesbone.common.server.IServer;

/**
 * 时间服务器NIO 实现
 * 
 * @author miles
 * @date 2017-05-01 下午4:14:58
 */
public class TimeServer implements IServer,Runnable{

	private static final Logger logger = LoggerFactory.getLogger(TimeServer.class);

	private ServerSocketChannel serverChannel;

	private Selector selector;

	private volatile boolean alive = true;
	
	private String hostname;//服务的IP
	
	private String port;//服务端口

	private static final String TIME_CMD = "time";

	private static final String HALT_CMD = "halt";

	private static final String ERROR = "error";

	/**
	 * 
	 */
	public TimeServer() {
		super();
	}

	
	
	/**
	 * @param hostname
	 * @param port
	 */
	public TimeServer(String hostname, String port) {
		super();
		this.hostname = hostname;
		this.port = port;
	}




	public void start() throws Exception {
		
		start(hostname,0);
		
	}
	
	public void start(int port) throws IOException {
		start(this.hostname,port);
	}
	
	public void start(String hostname, int port) throws IOException {
		if(port < 0 || port > 65535){
			logger.error("服务器端口参数不合法,值应在0到65535之间,实际值为{}", port);
			throw new IllegalArgumentException("服务器端口参数不合法");
		}
		Properties timePorp = TimeServerConfiguration.getInstance().getTimeServerProp();
		
		if(StringUtils.isBlank(hostname)){
			hostname = timePorp.getProperty(Configuration.DEFAULT_HOST_NAME.getKey(), Configuration.DEFAULT_HOST_NAME.getValue());
		}
		
		if(port == 0){
			port = Integer.parseInt(timePorp.getProperty(Configuration.DEFAULT_PORT.getKey(), Configuration.DEFAULT_PORT.getValue()));
		}
		
		logger.info("正在启动时间服务器服务..");
		selector = Selector.open();

		serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		serverChannel.bind(new InetSocketAddress(hostname, port));
		// interested only in accept event
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		while (alive) {
			if (selector.select() < 0) {// no event
				continue;
			}

			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey selectionKey = (SelectionKey) it.next();
				it.remove();

				try {
					if (!selectionKey.isValid()) {
						continue;
					}
					if (selectionKey.isAcceptable()) { // new channel incomming
						SocketChannel sc = ((ServerSocketChannel) selectionKey.channel()).accept();

						// ignore if register failed
						if (!registerChannel(selector, sc, SelectionKey.OP_READ)) {
							continue;
						}
						logger.info("new channel registered {}", sc.getRemoteAddress().toString());

						// 处理客户端请求
						if (selectionKey.isReadable()) {
							handleRead(selectionKey);
						}

						if (selectionKey.isWritable()) {
							handleWrite(selectionKey);
						}
					}
				} catch (Exception e) {
					logger.error("处理NIO请求失败:{}", e);
				}
			}

		}
		if (selector != null) {
			try {
				selector.close();
			} catch (Exception e) {
				logger.error("关闭selector失败:{}", e);
			}
		}
	}

	/**
	 * 处理写请求
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	private void handleWrite(SelectionKey selectionKey) throws IOException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();

		try {
			ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
			if (buffer != null) {
				writeBytesToChannel(channel, buffer, selectionKey);
			}
		} catch (ClassCastException e) {
			logger.error("类转换失败:{}", e);
		}
	}

	/**
	 * 
	 * @param channel
	 * @param buffer
	 * @param selectionKey
	 * @throws IOException
	 */
	private void writeBytesToChannel(SocketChannel channel, ByteBuffer buffer, SelectionKey selectionKey)
			throws IOException {
		if (!buffer.hasRemaining()) {
			return;
		}

		int total = buffer.remaining();
		int write = channel.write(buffer);
		// didn't wrote all, then write rest when next event triggered
		if (write < total) {
			selectionKey.attach(buffer);
		}
	}

	/**
	 * 处理客户端读操作
	 * 
	 * @param selectionKey
	 * @throws IOException
	 */
	private void handleRead(SelectionKey selectionKey) throws IOException {
		SocketChannel channel = (SocketChannel) selectionKey.channel();
		ByteBuffer buffer = ByteBuffer.allocate(16);
		int read = channel.read(buffer);

		// not a full command, write error back, meaning client will send
		// command again.
		if (read < 4) {
			writeBytesToChannel(channel, ERROR.getBytes(), selectionKey);
			logger.error("not a full command, write error back");
		} else {
			String cmd = extractCommand(buffer);
			logger.info("recieve {} request from {}", cmd, channel.getRemoteAddress().toString());
			if (TIME_CMD.equalsIgnoreCase(cmd)) {
				writeBytesToChannel(channel, String.valueOf(time()).getBytes(), selectionKey);
				logger.info("write time to {}", channel.getRemoteAddress().toString());
			} else if (HALT_CMD.equalsIgnoreCase(cmd)) {
				// 停止服务
				logger.info("stopping timeserve");
				stop();
				logger.info("timeserve stoped");
			} else {
				writeBytesToChannel(channel, ERROR.getBytes(), selectionKey);
				logger.warn("unreconized command {}, will discard it.", cmd);
			}
		}
	}

	private long time() {
		return System.currentTimeMillis();
	}

	private String extractCommand(ByteBuffer buffer) {
		buffer.flip();
		byte[] array = buffer.array();
		byte[] newArray = new byte[buffer.remaining()];
		System.arraycopy(array, buffer.position(), newArray, 0, buffer.remaining());
		return new String(newArray);
	}

	/**
	 * 
	 * @param channel
	 * @param bytes
	 * @param selectionKey
	 * @throws IOException
	 */
	private void writeBytesToChannel(SocketChannel channel, byte[] bytes, SelectionKey selectionKey)
			throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		int total = buffer.remaining();
		int write = channel.write(buffer);
		// didn't wrote all, then write rest when next event triggered
		if (write < total) {
			selectionKey.attach(buffer);
		}
	}

	/**
	 * 停止服务
	 */
	public void stop() {
		alive = false;

		try {
			serverChannel.close();
			selector.close();
		} catch (Exception e) {
			logger.error("停止服务失败:{}", e);
		}
	}

	/**
	 * 监听注册事件
	 * 
	 * @param selector
	 * @param sc
	 * @param ops
	 * @return
	 */
	private boolean registerChannel(Selector selector, SocketChannel sc, int ops) {
		try {
			sc.configureBlocking(false);
			sc.register(selector, ops);
		} catch (Exception e) {
			logger.error("注册通道");
			return false;
		}
		return true;
	}

	


	@Override
	public void restart() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void run() {
		try {
			start();
		} catch (Exception e) {
			logger.error("线程启动失败");
			e.printStackTrace();
		}
	}
	
}
