package com.milesbone.zookeeper.election;


/**
 * 选举
 * @author miles
 * @date 2017-03-08 下午9:24:35
 */
public interface LeaderElection {
	/**
	 * 开始选举
	 * @throws Exception
	 */
	public void leaderElectionStart() throws Exception;	
}
