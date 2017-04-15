package com.milesbone.zookeeper.util;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public interface IZookeeperUtil {
	
	/**
	 * 创建连接
	 */
	void create();

	/**
	 * 创建节点
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	String createNode(String path, String data, List<ACL> acl, CreateMode createMode)
			throws KeeperException, InterruptedException;

	
	/**
	 * 异步创建节点
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @return
	 * @throws InterruptedException 
	 */
	void createNodeAsync(String path, String data, List<ACL> acl, CreateMode createMode);
	
	/**
	 * 判断Zookeeper节点是否存在
	 * @param path
	 * @param watch
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	Stat existNode(String path, Watcher watch) throws KeeperException, InterruptedException;

	/**
	 * 强制删除zookeeper节点
	 * @param path
	 * @param version
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	void deleteNode(String path, int version) throws InterruptedException, KeeperException;
	
	
	/**
	 * 级联删除zookeeper节点
	 * @param path
	 * @param version
	 * @throws InterruptedException
	 * @throws KeeperException
	 */
	void deleteNodeCascade(String path, int version) throws InterruptedException, KeeperException;

	/**
	 * 获取zookeeper子节点
	 * @param path
	 * @param watcher
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException;

	/**
	 * 读取zookeeper数据
	 * @param path
	 * @param watch
	 * @param stat
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	byte[] getData(String path, Watcher watch, Stat stat) throws KeeperException, InterruptedException;

	
	/**
	 * 更新zookeeper节点数据
	 * @param path
	 * @param data
	 * @param version
	 * @return
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	Stat setData(String path, byte[] data,int version) throws KeeperException, InterruptedException;
	
	
	/**
	 * 获取最新版本并更新数据
	 * @param path
	 * @param data
	 * @return
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	Stat setData(String path, byte[] data) throws KeeperException, InterruptedException;
	
	/**
	 * 关闭连接
	 */
	void close();

}