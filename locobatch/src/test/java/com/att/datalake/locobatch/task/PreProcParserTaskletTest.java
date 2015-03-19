package com.att.datalake.locobatch.task;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
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

import com.att.datalake.locobatch.job.LocoJob;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { PreProcParserTaskletTest.class })
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan({ "com.att.datalake.locobatch.step", "com.att.datalake.locobatch.task",
		"com.att.datalake.locobatch.job", "com.att.datalake.loco.preproc", "com.att.datalake.loco.sqlgenerator",
		"com.att.datalake.loco.offerconfiguration" })
public class PreProcParserTaskletTest extends AbstractTestNGSpringContextTests {

	@Bean
	public Gson gson() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}

	@Autowired
	private LocoJob job;
	@Autowired
	JobLauncher launcher;

	@Test
	public void jobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		System.out.println("Running the test");
		launcher.run(job.preProcessingJob(), new JobParameters());
	}
}