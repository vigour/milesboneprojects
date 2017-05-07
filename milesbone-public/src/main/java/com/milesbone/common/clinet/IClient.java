package com.milesbone.common.clinet;



/**
 * 通用的客户端接口
 * @author miles
 * @date 2017-05-06 下午5:36:07
 */
public interface IClient {
	/**
	 * 启动客户端
	 * @throws Exception
	 */
	public void start() throws Exception;

	
	/**
	 * 停止客户端
	 * @throws Exception
	 */
	public void stop() throws Exception;

	/**
	 * 重启客户端
	 * @throws Exception
	 */
	public void restart() throws Exception;
}
