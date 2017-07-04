package com.milesbone.common.test.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
//配置了@ContextConfiguration注解并使用该注解的locations属性指明spring和配置文件之后，
@ContextConfiguration(locations = {"classpath:spring-*.xml" })
public abstract class AbstractControllerTestCase {
	   	@Autowired
	    private WebApplicationContext applicationContext;
	   	
	   	@Autowired
	   	protected MockHttpSession session;
	   	
	   	@Autowired
	   	protected MockHttpServletRequest request;
	   	
	   	protected MockMvc mockMvc;

	    @Before
	    public void setUp() {
	        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	    }

	   
	    
	    
}
