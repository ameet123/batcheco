package com.att.datalake.loco.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.batch.step.Step11;
import com.att.datalake.loco.batch.step.Step12;
import com.att.datalake.loco.batch.step.Step13;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;

/**
 * test preprocessing parser logic
 * we need to exclude other beans from loading
 * @author ac2211
 *
 */
@Configuration
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { LocoBatchJobTest.class })
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.att.datalake.loco.batch.service", "com.att.datalake.loco.batch.step",
		"com.att.datalake.loco.batch.shared", "com.att.datalake.loco.batch.listener",
		"com.att.datalake.loco.batch.task", "com.att.datalake.loco.batch.job", "com.att.datalake.loco.preproc",
		"com.att.datalake.loco.sqlgenerator", "com.att.datalake.loco.offerconfiguration" }, 
		excludeFilters = { 
			@ComponentScan.Filter(type = FilterType.REGEX, pattern = { ".*(Step0|Step20|HiveProcessorTasklet|CriteriaLoaderTasklet|LocoMrOnlyJob).*" }) 
		})
public class LocoBatchJobTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step11 step11;
	@Autowired
	private Step12 step12;
	@Autowired
	private Step13 step13;
	@Autowired
	JobLauncher launcher;
	@Autowired
	private OfferDAO offerDao;
	
	private String offerId = "ABCDEFGHIJ1234567893";
	
	@BeforeClass
	public void init() {
		Offer o = new Offer();
		o.setOfferId(offerId);
		Offer saved = offerDao.saveOffer(o);
		Assert.state(saved.getOfferId().equals(offerId));
	}

	@Test
	public void jobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Job job = jobBuilders.get("Job:Preprocessing parser job").incrementer(new RunIdIncrementer()).flow(step11.build())
				.next(step12.build()).next(step13.build()).end().build();
		System.out.println("Running the test");
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		launcher.run(job, jobParameters);
	}

	@AfterClass
	public void testDelete() {
		offerDao.deleteOffer(offerId);
		Assert.state(offerDao.countOffers()==0);
	}
}