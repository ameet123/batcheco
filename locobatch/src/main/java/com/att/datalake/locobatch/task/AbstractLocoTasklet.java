package com.att.datalake.locobatch.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class AbstractLocoTasklet implements Tasklet {
	
	@Override
	public RepeatStatus execute(StepContribution contrib, ChunkContext context) throws Exception {
		process();
		return RepeatStatus.FINISHED;
	}

	public abstract String getName();
	public abstract String getDescr();
	public abstract void process();
}