package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.CriteriaSqlExtractor;

/**
 * data extraction to local
 * 
 * @author ac2211
 *
 */
@Component
public class Step30 extends AbstractLocoStep {

	private final String stepName = "step-30:offer-extraction-runner";
	private final String stepDescr = "extract data to local disk";

	@Autowired
	private CriteriaSqlExtractor tasklet30;


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
		return tasklet30;
	}
}