package com.att.datalake.locobatch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreValidationTasklet;

@Component
public class Step12 extends AbstractLocoStep {
	private final String STEP_NAME = "step-12:preproc-file-validation";
	private final String STEP_DESCR = "after processing the preprocessing syntax file, validate the parsing into object, ensure that columns are not broken into unintentional fragments";

	@Autowired
	private PreValidationTasklet tasklet12;


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
		return tasklet12;
	}
}