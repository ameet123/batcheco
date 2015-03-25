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

	private final String stepName = "step-20:preproc-sql-hive-query-execution";
	private final String stepDescr = "run hive queries for all the offers based on preprocessing parsing done";

	@Autowired
	private HiveProcessorTasklet tasklet20;


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
		return tasklet20;
	}
}