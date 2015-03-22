package com.att.datalake.locobatch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreProcessorParserTasklet;

@Component
public class Step1 extends AbstractLocoStep {
	
	private final String STEP_NAME = "step-1:preproc-file-processing";
	private final String STEP_DESCR = "given a file, process it into a schema object";

	@Autowired
	private PreProcessorParserTasklet tasklet1;

	@Value("${preproc.file:input/preproc.csv}")
	private String preprocCsv;

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
		tasklet1.setFile(preprocCsv);
		return tasklet1;
	}
}
