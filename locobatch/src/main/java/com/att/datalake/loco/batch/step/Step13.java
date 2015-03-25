package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.PreProcSqlgenTasklet;

/**
 * for sql generation
 * 
 * @author ac2211
 *
 */
@Component
public class Step13 extends AbstractLocoStep {

	private final String STEP_NAME = "step-13:preproc-sql-generation";
	private final String STEP_DESCR = "generate sql for each offer based on parsed syntax file and store it in a bean for downstream processing";

	@Autowired
	private PreProcSqlgenTasklet tasklet13;


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
		return tasklet13;
	}
}