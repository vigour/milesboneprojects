package com.milesbone.common.exception;

import java.net.SocketAddress;

/**
 * 时间服务器客户端异常
 * 
 * @author miles
 * @date 2017-05-01 下午11:59:24
 */
public class TimeClientException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2137264165157620165L;

	/**
	 * 
	 */
	public TimeClientException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public TimeClientException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param address
	 */
	public TimeClientException(SocketAddress address) {
		super(address.toString());
	}

}
