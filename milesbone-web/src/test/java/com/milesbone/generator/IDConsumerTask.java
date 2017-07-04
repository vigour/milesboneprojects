package com.milesbone.generator;

public class IDConsumerTask implements Runnable{

	
	private IIdGenerator idGenerator;
	
	private String name;
	
	
	/**
	 * 
	 */
	public IDConsumerTask() {
		super();
	}


	
	


	/**
	 * @param idGenerator
	 * @param name
	 */
	public IDConsumerTask(IIdGenerator idGenerator, String name) {
		super();
		this.idGenerator = idGenerator;
		this.name = name;
	}






	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
