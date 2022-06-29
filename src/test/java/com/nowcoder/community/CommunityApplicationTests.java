package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootTest
class CommunityApplicationTests implements ApplicationContextAware {
	ApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	void testService(){
		AlphaService service = applicationContext.getBean(AlphaService.class);
		System.out.println(service);
	}

	@Test
	void testDao(){
		AlphaDao alphaDao = applicationContext.getBean("alphaDaoImp2", AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Autowired
	@Qualifier("alphaDaoImp2")
	private AlphaDao alphaDao;

	@Test
	void testDI(){
		System.out.println(alphaDao.select());
	}
}
