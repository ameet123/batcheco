package com.att.datalake.loco.batch.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorld implements Tasklet{

	@Override
	public RepeatStatus execute(StepContribution contrib, ChunkContext context) {
		System.out.println("**** HELLO World! ****");
		System.out.println("START:"+context.getStepContext().getStepExecution().getStartTime());
		return RepeatStatus.FINISHED;
	}

}
