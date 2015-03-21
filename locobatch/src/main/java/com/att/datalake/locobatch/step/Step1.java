package com.att.datalake.locobatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreProcessorParserTasklet;
import com.att.datalake.locobatch.task.PreValidationTasklet;

@Component
public class Step1 {
	private static final Logger LOGGER = LoggerFactory.getLogger(Step1.class);
	
	private final String STEP_NAME = "step-1:preproc-file-processing";
	
	@Autowired
	private StepBuilderFactory stepBuilders;
	@Autowired
	private PreProcessorParserTasklet tasklet1;
	@Autowired
	private PreValidationTasklet tasklet2;
	@Value("${preproc.file:input/preproc.csv}")
	private String preprocCsv;
	
	public Step build() {
		LOGGER.debug("Preprocessing file:{}", preprocCsv);
		tasklet1.setFile(preprocCsv);
		return stepBuilders.get(STEP_NAME).allowStartIfComplete(true).tasklet(tasklet1).build();
	}
}
