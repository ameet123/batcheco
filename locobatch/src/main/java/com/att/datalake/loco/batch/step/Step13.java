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

	private final String stepName = "step-13:preproc-sql-generation";
	private final String stepDescr = "generate sql for each offer based on parsed syntax file and store it in a bean for downstream processing";

	@Autowired
	private PreProcSqlgenTasklet tasklet13;


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
		return tasklet13;
	}
}