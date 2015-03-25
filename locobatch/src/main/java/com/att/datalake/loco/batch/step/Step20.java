package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.HiveProcessorTasklet;

/**
 * for sql generation
 * 
 * @author ac2211
 *
 */
@Component
public class Step20 extends AbstractLocoStep {

	private final String STEP_NAME = "step-20:preproc-sql-hive-query-execution";
	private final String STEP_DESCR = "run hive queries for all the offers based on preprocessing parsing done";

	@Autowired
	private HiveProcessorTasklet tasklet20;


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
		return tasklet20;
	}
}