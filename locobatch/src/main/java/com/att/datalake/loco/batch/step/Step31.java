package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.DataPackerTasklet;

/**
 * data consolidation and compression
 * 
 * @author ac2211
 *
 */
@Component
public class Step31 extends AbstractLocoStep {

	private final String stepName = "step-31:offer-data-packer";
	private final String stepDescr = "pack data for shipping , possibly compressing it";

	@Autowired
	private DataPackerTasklet tasklet31;


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
		return tasklet31;
	}
}