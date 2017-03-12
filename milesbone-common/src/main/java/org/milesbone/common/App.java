package org.milesbone.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	logger.trace("this is trace mode");
    	logger.debug("this is Debug mode","aaaaa");
    	logger.info("this is info mode");
    	logger.warn("this is warn mode");
    	logger.error("this is error mode");
    }
}
