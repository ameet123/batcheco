package com.att.datalake.loco.batch.task;

import org.springframework.batch.core.step.tasklet.Tasklet;

public interface CommonTasklet extends Tasklet{

	void process();
	String getName();
}
