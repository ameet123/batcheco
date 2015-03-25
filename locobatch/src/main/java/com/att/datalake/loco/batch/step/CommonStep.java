package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.tasklet.Tasklet;

public interface CommonStep {

	String getDescrition();
	String getName();
	Tasklet getTasklet();
	Step build();
}
