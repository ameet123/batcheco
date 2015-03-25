package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.CriteriaLoaderTasklet;

@Component
public class Step0 extends AbstractLocoStep {
	
	private final String stepName = "step-0:offer-criteria-loading";
	private final String stepDescr = "load the offer criteria file to the database, parse it, generate corresponding sql and persist it locally as well as in remote db for downstream processing and future reference";

	@Autowired
	private CriteriaLoaderTasklet tasklet0;

	private String filename;

	public Step0 setFilename(String filename) {
		this.filename = filename;
		return this;
	}
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
		tasklet0.setFilename(filename);
		return tasklet0;
	}
}