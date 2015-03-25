package com.att.datalake.loco.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.step.Step20;

//@Component
public class LocoMrOnlyJob {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step20 step20;
	
	@Value("${offer.file:input/offer.csv}")
	private String filename;

	public Job preProcessingJob() {
		return jobBuilders.get("Job:Pre-Processing sql hive execution job").incrementer(new RunIdIncrementer()).
				flow(step20.build()).				
				end().
				build();
	}
}