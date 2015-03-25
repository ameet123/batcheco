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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.shared.LocoConfiguration.RuntimeData;
import com.att.datalake.loco.batch.step.Step20;

/**
 * just test whether we can run hive query
 * NOTE: WE can't run it from eclipse, so this is not useful
 * @author ac2211
 *
 */
@Configuration
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { LocoMrOnlyJobTest.class })
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.att.datalake.loco.batch.service", "com.att.datalake.loco.batch.step",
		"com.att.datalake.loco.batch.shared", "com.att.datalake.loco.batch.listener", "com.att.datalake.loco.batch.task",
		"com.att.datalake.loco.batch.job", "com.att.datalake.loco.preproc", "com.att.datalake.loco.sqlgenerator",
		"com.att.datalake.loco.offerconfiguration"})
public class LocoMrOnlyJobTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step20 step20;
	@Bean
	public Job getMrTestingJob() {
		RuntimeData data = new RuntimeData();
		data.setPreProcSql("select count(*) from ameet.loco_offer1");
		config.set("a123", data);
		System.out.println("Running the test");
		
		return jobBuilders.get("Job:Pre-Processing sql hive execution job").incrementer(new RunIdIncrementer()).
		flow(step20.build()).				
		end().
		build();
	}	
	@Autowired
	private Job job;
	@Autowired
	JobLauncher launcher;
	@Autowired
	private LocoConfiguration config;
	@Test
	public void jobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		// add a sql to the config
		RuntimeData data = new RuntimeData();
		data.setPreProcSql("select count(*) from ameet.loco_offer1");
		config.set("a123", data);
		System.out.println("Running the test");
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		launcher.run(job, jobParameters);
	}
}