package com.milesbone.common.server;

/**
 * 通用服务器接口
 * 
 * @author miles
 * @date 2017-05-06 下午5:32:19
 */
public interface IServer {

	/**
	 * 启动服务器服务
	 * @throws Exception
	 */
	public void start() throws Exception;

	
	/**
	 * 停止服务器服务
	 * @throws Exception
	 */
	public void stop() throws Exception;

	/**
	 * 重启服务器服务
	 * @throws Exception
	 */
	public void restart() throws Exception;
}
