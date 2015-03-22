package com.att.datalake.locobatch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreProcSqlgenTasklet;

/**
 * for sql generation
 * 
 * @author ac2211
 *
 */
@Component
public class Step3 extends AbstractLocoStep {

	private final String STEP_NAME = "step-3:preproc-sql-generation";
	private final String STEP_DESCR = "generate sql for each offer based on parsed syntax file and store it in a bean for downstream processing";

	@Autowired
	private PreProcSqlgenTasklet tasklet3;


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
		return tasklet3;
	}
}