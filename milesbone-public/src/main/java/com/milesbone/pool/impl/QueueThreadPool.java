package com.milesbone.pool.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milesbone.task.IPoolCallableTask;
import com.milesbone.task.IPoolRunnableTask;
import com.milesbone.task.IPoolTask;


public class QueueThreadPool {

	private static final Logger logger = LoggerFactory.getLogger(QueueThreadPool.class);

	private ThreadPoolExecutor executor = null;// 线程池对象

	private List<IPoolTask> taskList = null;// 任务项

	private QueueThreadPoolMonitor monitor;// 监控项

	private long timeout = 300 * 1000;// 默认超时时间

	private boolean isStop = false; // 是否停止

	private String poolId = ""; // 线程池ID

	private int coreThreadNum = 10;// 核心线程数

	private int maxThreadNum = 10;// 最大线程数

	private long keepAlive = 10000L;// 空闲线程保存时间

	private int cacheNum = 30;// 缓存任务数量

	public QueueThreadPool() {

	}

	public void init(String poolId, int coreThreadNum, int maxThreadNum, long keepAlive) {
		logger.info("开始启动线程池: {}", poolId);
		this.poolId = poolId;
		this.coreThreadNum = (coreThreadNum == 0 ? this.coreThreadNum : coreThreadNum);
		this.maxThreadNum = (maxThreadNum == 0 ? this.maxThreadNum : maxThreadNum);
		this.cacheNum = (cacheNum == 0 ? this.cacheNum : cacheNum);
		this.keepAlive = (keepAlive == 0 ? this.keepAlive : keepAlive);
		executor = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, keepAlive, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<Runnable>(cacheNum), new ThreadPoolExecutor.CallerRunsPolicy());

	}
	
	/**
	 * 提交任务 带监控时间
	 * @param task
	 */
	public void excute(IPoolRunnableTask task){
		synchronized (taskList) {
			if(executor != null)
				executor.execute(task);
			if(taskList != null)
				taskList.add(task);
		}
	}
	
	/**
	 * 提交任务不监控超时
	 * @param task
	 */
	public void executeWithOutTimeout(IPoolRunnableTask task) {
		synchronized (taskList) {
			executor.execute(task);
			if(executor != null)
				executor.execute(task);
		}
	}
	
	
	/**
	 * 提交任务
	 * @param task
	 * @return
	 */
	public Future submit(IPoolRunnableTask task){
		synchronized (taskList) {
			if(executor != null){
				Future f = executor.submit(task);
				taskList.add(task);
				return f;
			}
			return null;
		}
		
	}
	
	/**
	 * 提交任务
	 * @param task
	 * @return
	 */
	public Future submit(IPoolCallableTask task){
		synchronized (taskList) {
			if(executor != null){
				Future f = executor.submit(task);
				taskList.add(task);
				return f;
			}
			return null;
		}
	}

	/**
	 * 移除任务
	 * @param task
	 */
	public void removeTask(IPoolTask task){
		synchronized (taskList) {
			if(taskList != null)
				taskList.remove(task);
		}
	}
	
	
	/**
	 * 关闭线程池
	 */
	public void stop(){
		if(!isStop){
			if(monitor != null){
				monitor.stop();
			}
			
			if(executor != null){
				executor.shutdownNow();
				taskList = null;
				isStop = true;
			}
		}
	}
	
	
	public String getPoolId() {
		return poolId;
	}

	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}

	public List<IPoolTask> getTaskList() {
		return taskList;
	}

	public long getTimeout() {
		return timeout;
	}

	/**
	 * 监控线程池类
	 * @author miles
	 * @date 2016-08-29 下午3:16:36
	 */
	public class QueueThreadPoolMonitor implements Runnable {
		private QueueThreadPool pool = null;

		private boolean runflag = false;

		private long sleepTime = 120 * 1000;

		public QueueThreadPoolMonitor() {
		}

		public QueueThreadPoolMonitor(QueueThreadPool pool) {
			super();
			this.pool = pool;
		}

		/**
		 * 监控启动线程池
		 */
		public void start() {
			if (!runflag) {
				logger.info("开始启动线程池{}的监控器.....", poolId);
				this.runflag = true;
				Thread t = new Thread(this);
				t.start();
				logger.info("启动线程池{}的监控器完毕", poolId);
			}
		}

		public void stop() {
			this.runflag = false;
		}

		public void run() {
			while (runflag) {
				try {
					if (pool == null) {
						runflag = false;
					}

					// 获取所有监控任务
					List<IPoolTask> list = pool.getTaskList();

					// 待关闭任务
					List<IPoolTask> removeList = new ArrayList<IPoolTask>();
					synchronized (list) {
						if (list != null && list.size() > 0) {
							for (IPoolTask task : list) {
								if (!task.getRunFlag()) {
									removeList.add(task);
									continue;
								}

								// 超时时间
								long tmout = pool.getTimeout();
								// 任务开始时间
								long startTim = task.getStartTime();
								// 系统当前时间
								long curTim = System.currentTimeMillis();
								long t = curTim - startTim;
								if (t > tmout) {
									removeList.add(task);
								} else {
									continue;
								}
							}

							for (IPoolTask task : removeList) {
								Thread t = task.getThread();
								if (!task.getRunFlag()) {
									list.remove(task);
									continue;
								}
								if (t != null && t.isAlive()) {
									try {
										logger.info(task.getThreadId() + "：超时，尝试关闭......");
										t.stop();
										list.remove(task);
										logger.info(task.getThreadId() + "：超时，已经关闭");
									} catch (Throwable e) {
									}
								}

							}
							removeList = null;
						}
					}
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
	}

}
