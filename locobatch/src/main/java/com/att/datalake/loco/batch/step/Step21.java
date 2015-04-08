package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.OfferSqlExecutorTasklet;

/**
 * for sql generation
 * 
 * @author ac2211
 *
 */
@Component
public class Step21 extends AbstractLocoStep {

	private final String stepName = "step-21:offer-sql-runner";
	private final String stepDescr = "run hive queries for all the offers based on offer criteria and insert the records into final offer table";

	@Autowired
	private OfferSqlExecutorTasklet tasklet21;


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
		return tasklet21;
	}
}