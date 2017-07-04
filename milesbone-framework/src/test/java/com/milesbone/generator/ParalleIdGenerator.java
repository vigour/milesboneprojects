package com.milesbone.generator;

import java.util.concurrent.CountDownLatch;

public class ParalleIdGenerator implements Runnable {

	private CountDownLatch signal;
	
	private CountDownLatch finish;
	
	private int taskNumber = 0;
	
	private IIdGenerator generator;

	@Override
	public void run() {

	}

}
