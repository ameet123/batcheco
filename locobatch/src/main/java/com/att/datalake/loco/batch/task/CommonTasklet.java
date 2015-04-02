package com.att.datalake.loco.batch.task;

import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;

public interface CommonTasklet extends Tasklet{

	void process(ChunkContext context);
	String getName();
}
