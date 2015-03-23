package com.att.datalake.locobatch.task;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.att.datalake.locobatch.job.LocoJob;
/**
 * eclude filter is not workin
 * , excludeFilters={ @Filter(type=FilterType.ASSIGNABLE_TYPE, value={com.att.datalake.locobatch.task.HiveProcessorTasklet.class} ) }
 * @author ac2211
 *
 */
@Configuration
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { LocoBatchJobTest.class })
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages={ "com.att.datalake.locobatch.service", "com.att.datalake.locobatch.step",
		"com.att.datalake.locobatch.shared", "com.att.datalake.locobatch.listener", "com.att.datalake.locobatch.task",
		"com.att.datalake.locobatch.job", "com.att.datalake.loco.preproc", "com.att.datalake.loco.sqlgenerator",
		"com.att.datalake.loco.offerconfiguration" })
public class LocoBatchJobTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private LocoJob job;
	@Autowired
	JobLauncher launcher;

	@Test
	public void jobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		System.out.println("Running the test");
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		launcher.run(job.preProcessingJob(), jobParameters);
	}
}