package com.att.datalake.locobatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.step.PreProcFileProcessingStep;

@Component
public class LocoJob {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private PreProcFileProcessingStep step1;

	
	public Job preProcessingJob() {
		return jobBuilders.get("Job:Preprocessing parser job").incrementer(new RunIdIncrementer()).flow(step1.build())
				.end().build();
	}
}
