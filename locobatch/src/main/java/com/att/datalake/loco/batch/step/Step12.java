package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.PreValidationTasklet;

@Component
public class Step12 extends AbstractLocoStep {
	private final String stepName = "step-12:preproc-file-validation";
	private final String stepDescr = "after processing the preprocessing syntax file, validate the parsing into object, ensure that columns are not broken into unintentional fragments";

	@Autowired
	private PreValidationTasklet tasklet12;


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
		return tasklet12;
	}
}