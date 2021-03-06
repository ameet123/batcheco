package com.att.datalake.loco.batch.task;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class Hello2 implements Tasklet{

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) {
		System.out.println("**** HELLO Again - 2 ! ****");
		return RepeatStatus.FINISHED;
	}

}
