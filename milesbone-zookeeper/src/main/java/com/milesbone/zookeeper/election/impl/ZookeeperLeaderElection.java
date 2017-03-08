package com.milesbone.zookeeper.election.impl;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.milesbone.zookeeper.election.LeaderElection;

public class ZookeeperLeaderElection implements LeaderElection,Watcher{

	@Override
	public void leaderElectionStart() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}

}
