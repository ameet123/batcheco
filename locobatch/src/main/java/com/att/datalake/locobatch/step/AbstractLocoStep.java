package com.att.datalake.locobatch.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractLocoStep implements CommonStep {

	@Autowired
	private StepExecutionListener monitor;
	@Autowired
	private StepBuilderFactory stepBuilders;

	public Step build() {
		return stepBuilders.get(getName()).allowStartIfComplete(true).tasklet(getTasklet()).listener(monitor).build();
	}
}