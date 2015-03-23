package com.att.datalake.locobatch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.CriteriaVerifierTasklet;

@Component
public class Step1 extends AbstractLocoStep {
	
	private final String STEP_NAME = "step-1:offer-criteria-verification";
	private final String STEP_DESCR = "verify that the criteria sql is available in local and db configuration";

	@Autowired
	private CriteriaVerifierTasklet tasklet1;

	@Override
	public String getDescrition() {
		return STEP_DESCR;
	}

	@Override
	public String getName() {
		return STEP_NAME;
	}

	@Override
	public Tasklet getTasklet() {
		return tasklet1;
	}
}