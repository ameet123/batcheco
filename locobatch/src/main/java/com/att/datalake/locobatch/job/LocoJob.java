package com.att.datalake.locobatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.step.Step11;
import com.att.datalake.locobatch.step.Step12;
import com.att.datalake.locobatch.step.Step13;

@Component
public class LocoJob {

	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step11 step11;
	@Autowired
	private Step12 step12;
	@Autowired
	private Step13 step13;

	public Job preProcessingJob() {
		return jobBuilders.get("Job:Preprocessing parser job").incrementer(new RunIdIncrementer()).
				flow(step11.build()).
				next(step12.build()).
				next(step13.build()).
				end().
				build();
	}
}