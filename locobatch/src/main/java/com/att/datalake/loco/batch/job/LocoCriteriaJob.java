package com.att.datalake.loco.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.step.Step0;
import com.att.datalake.loco.batch.step.Step1;

@Component
public class LocoCriteriaJob {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step0 step0;
	@Autowired
	private Step1 step1;
	
	@Value("${offer.file:input/offer.csv}")
	private String filename;

	public Job preProcessingJob() {
		return jobBuilders.get("Job:Offer Criteria Processing job").incrementer(new RunIdIncrementer()).
				flow(step0.setFilename(filename).build()).
				next(step1.build()).
				end().
				build();
	}
}