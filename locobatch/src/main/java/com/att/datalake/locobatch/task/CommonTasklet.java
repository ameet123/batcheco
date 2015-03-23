package com.att.datalake.locobatch.task;

import org.springframework.batch.core.step.tasklet.Tasklet;

public interface CommonTasklet extends Tasklet{

	void process();
	String getName();
}
