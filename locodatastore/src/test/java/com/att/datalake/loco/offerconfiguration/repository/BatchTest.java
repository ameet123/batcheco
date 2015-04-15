package com.att.datalake.loco.offerconfiguration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.att.datalake.loco.ContextConfig;

/**
 * test db interactions using jpa
 * @author ac2211
 *
 */
@ContextConfiguration(	loader = AnnotationConfigContextLoader.class,
classes = { ContextConfig.class})
public class BatchTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private BatchJobDAO batchDao;
	
	@Test
	public void testCnt() {
		long cnt  = batchDao.countJobs();
		System.out.println("Jobs:"+cnt);
	}
}