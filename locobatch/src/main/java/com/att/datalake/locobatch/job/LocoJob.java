package com.att.datalake.locobatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.step.Step1;
import com.att.datalake.locobatch.step.Step2;
import com.att.datalake.locobatch.step.Step3;

@Component
public class LocoJob {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step1 step1;
	@Autowired
	private Step2 step2;
	@Autowired
	private Step3 step3;

	public Job preProcessingJob() {
		return jobBuilders.get("Job:Preprocessing parser job").incrementer(new RunIdIncrementer()).
				flow(step1.build()).
				next(step2.build()).
				next(step3.build()).
				end().
				build();
	}
}