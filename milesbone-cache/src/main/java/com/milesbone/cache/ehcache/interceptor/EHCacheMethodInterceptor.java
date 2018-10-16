package com.milesbone.cache.ehcache.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.milesbone.util.SerializationUtil;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;



/**
 * 拦截缓存服务拦截器
 * @Title  EHCacheMethodInterceptor.java
 * @Package com.milesbone.cache.ehcache.interceptor
 * @Description    TODO
 * @author miles
 * @date   2018-10-09 11:41
 */
public class EHCacheMethodInterceptor implements MethodInterceptor, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(EHCacheMethodInterceptor.class);
	
	private Cache cache;
	
	public EHCacheMethodInterceptor() {
		super();
	}
	
	
	public void setCache(Cache cache) {
		this.cache = cache;
	}
	
	
	public void afterPropertiesSet() throws Exception {
		if (cache == null) {
			logger.error("缓存值为空需要创建新缓存");
			throw new IllegalArgumentException("Need a cache. Please use setCache(Cache) create it.");
		}
	}
	
	

	 /**
     * 拦截ServiceManager的方法，并查找该结果是否存在，如果存在就返回cache中的值，
     * 否则，返回数据库查询结果，并将查询结果放入cache
     */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//获取要拦截的类
		String targetName = invocation.getThis().getClass().getName();
		//获取要拦截的方法
		String methodName = invocation.getMethod().getName();
		//获取要拦截的类的方法参数
		Object[] arguments = invocation.getArguments();
		
		Object result;
		
		//创建一个字符串,用来做cache中的key
		String cacheKey = getCacheKey(targetName, methodName, arguments);
		
		//从cache中获取数据
		Element element = cache.get(cacheKey);
		
		
		if(element == null) {
			//如果cache中没有数据，则查找非缓存，例如数据库，并将查找到的放入cache
			
			result = invocation.proceed();
			
			element = new Element(cacheKey, SerializationUtil.serialize(result));
			logger.debug("-----进入非缓存中查找，例如直接查找数据库，查找后放入缓存");
			
			//将key和value存入cache
			cache.put(element);
		}else {
			logger.debug("-----进入缓存中查找，不查找数据库，缓解了数据库的压力");
		}
		return element.getObjectValue();
	}


	/**
	 * 组装缓存的key值
	 * 获得cache的key的方法，cache的key是Cache中一个Element的唯一标识，
     * 包括包名+类名+方法名，如：com.test.service.TestServiceImpl.getObject
	 * @param targetName
	 * @param methodName
	 * @param argument
	 * @return
	 */
	private String getCacheKey(String targetName, String methodName, Object[] arguments) {
		StringBuffer sb = new StringBuffer();
        sb.append(targetName).append(".").append(methodName);
        if ((arguments != null) && (arguments.length != 0)) {
            for (int i = 0; i < arguments.length; i++) {
                sb.append(".").append(arguments[i]);
            }
        }
        return sb.toString();
	}

}
