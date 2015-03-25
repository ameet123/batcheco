package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.CriteriaVerifierTasklet;

@Component
public class Step1 extends AbstractLocoStep {
	
	private final String stepName = "step-1:offer-criteria-verification";
	private final String stepDescr = "verify that the criteria sql is available in local and db configuration";

	@Autowired
	private CriteriaVerifierTasklet tasklet1;

	@Override
	public String getDescrition() {
		return stepDescr;
	}

	@Override
	public String getName() {
		return stepName;
	}

	@Override
	public Tasklet getTasklet() {
		return tasklet1;
	}
}