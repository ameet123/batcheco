package com.att.datalake.loco.batch.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public abstract class AbstractLocoTasklet implements CommonTasklet {
	
	@Override
	public RepeatStatus execute(StepContribution contrib, ChunkContext context) {
		process();
		return RepeatStatus.FINISHED;
	}

	public abstract String getName();
	public abstract void process();
}