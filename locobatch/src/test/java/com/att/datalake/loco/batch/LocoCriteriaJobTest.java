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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.att.datalake.loco.batch.step.Step0;
import com.att.datalake.loco.batch.step.Step1;

/**
 * test offer criteria parsing
 * 
 * @author ac2211
 *
 */
@Configuration
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { LocoCriteriaJobTest.class })
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.att.datalake.loco.batch.service", "com.att.datalake.loco.batch.step",
		"com.att.datalake.loco.batch.shared", "com.att.datalake.loco.batch.listener",
		"com.att.datalake.loco.batch.task", "com.att.datalake.loco.batch.job", "com.att.datalake.loco.preproc",
		"com.att.datalake.loco.sqlgenerator", "com.att.datalake.loco.offerconfiguration",
		"com.att.datalake.loco.offerconfiguration.repository", "com.att.datalake.loco.offercriteria" }, 
		excludeFilters = { 
			@ComponentScan.Filter(type = FilterType.REGEX, pattern = { ".*(Step20|HiveProcessorTasklet|LocoMrOnlyJob).*" }) 
		}
)
public class LocoCriteriaJobTest extends AbstractTestNGSpringContextTests {

	@Value("${offer.file:input/offer.csv}")
	private String filename;
	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step0 step0;
	@Autowired
	private Step1 step1;
	@Autowired
	JobLauncher launcher;

	@Test
	public void jobTest() throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		Job job = jobBuilders.get("Job:Offer Criteria Processing job").incrementer(new RunIdIncrementer())
				.flow(step0.setFilename(filename).build()).next(step1.build()).end().build();
		System.out.println("Running the test");
		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
				.toJobParameters();
		launcher.run(job, jobParameters);
	}
}