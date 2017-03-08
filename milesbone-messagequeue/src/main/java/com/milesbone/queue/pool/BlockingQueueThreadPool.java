package com.milesbone.queue.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务处理线程池
 * @author miles
 * @date 2016-08-24 下午10:52:42
 */
public class BlockingQueueThreadPool implements IPool {

	private final static Logger logger = LoggerFactory.getLogger(BlockingQueueThreadPool.class);
	
	private String poolName;
	
	private BlockingQueue<Runnable> queue;
	
	private ExecutorService excutor;
	
	
	
	public BlockingQueueThreadPool(String poolName, BlockingQueue<Runnable> queue, ExecutorService excutor) {
		super();
		this.poolName = poolName;
		this.queue = queue;
		this.excutor = excutor;
	}



	@Override
	public void execute(Runnable worker) {
		// TODO Auto-generated method stub

	}

}
