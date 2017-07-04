package com.milesbone.common.test.base;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.milesbone.common.server.impl.TimeServer;


@RunWith(SpringJUnit4ClassRunner.class)
//配置了@ContextConfiguration注解并使用该注解的locations属性指明spring和配置文件之后，
@ContextConfiguration(locations = {"classpath:spring-*.xml" })
public abstract class AbstactServerBeanTestCase{
	
	protected static final Logger logger = LoggerFactory.getLogger(AbstactServerBeanTestCase.class);
	protected static Thread thread;

	protected static CountDownLatch countDownLatch = null;
	
	@BeforeClass  
    public static void beforeClass(){  
		logger.debug("@beforeClass");  
		countDownLatch = new CountDownLatch(1);
		
        thread = new Thread(new TimeServer());
        try {
        	thread.setDaemon(true);
        	thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
	
	@Before
	public abstract void setup();
	
	@After
	public abstract void tearDown();
	
	@AfterClass  
    public static void afterClass(){  
		logger.debug("@afterClass"); 
		countDownLatch.countDown();
        if(thread.isAlive()){
        	thread.interrupt();
        }
    } 
}
