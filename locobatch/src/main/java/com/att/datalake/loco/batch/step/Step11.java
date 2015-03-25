package com.att.datalake.loco.batch.step;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.task.PreProcessorParserTasklet;

@Component
public class Step11 extends AbstractLocoStep {
	
	private final String stepName = "step-11:preproc-file-processing";
	private final String stepDescr = "given a file, process it into a schema object";

	@Autowired
	private PreProcessorParserTasklet tasklet11;

	@Value("${preproc.file:input/preproc.csv}")
	private String preprocCsv;

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
		tasklet11.setFile(preprocCsv);
		return tasklet11;
	}
}
