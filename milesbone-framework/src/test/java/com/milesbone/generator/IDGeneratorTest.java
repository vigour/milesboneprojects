package com.milesbone.generator;

import javax.annotation.Resource;

import org.junit.Test;

import com.milesbone.common.test.base.AbstactServerBeanTestCase;
import com.milesbone.generator.impl.DefaultIdGenerator;


public class IDGeneratorTest extends AbstactServerBeanTestCase{
	
	@Resource(name="idGenerator")
	private IIdGenerator idGenerator;
	
	
	
	
	@Override
	public void setup() {
		
	}
	@Override
	public void tearDown() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	@Test
	public void testFormat(){
		int a = 1;
		System.out.println(String.format("%08d", a));
		
	}
	
	
	@Test
	public void testNext() throws InterruptedException{
		System.out.println(idGenerator.next());
	}
	
	
	@Test
	public void test1() throws InterruptedException{
//		idGenerator = new DefaultIdGenerator();
//		logger.debug(idGenerator.next());
//		for (int i = 0; i < 50; i++) {
//			idGenerator.next();
//			if(i%1000==0){
//				Thread.sleep(100);
				System.out.println(idGenerator.next());
//			}
//		}
	}
	
	
	@Test
	public void test2() throws InterruptedException{
		idGenerator = new DefaultIdGenerator();
		
		for (int i = 0; i < 50; i++) {
//			idGenerator.next();
//			if(i%1000==0){
				Thread.sleep(100);
				System.out.println(idGenerator.next());
//			}
		}
	}
	
	
	
}
