package com.att.datalake.locobatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreProcessorParserTasklet;

@Component
public class PreProcFileProcessingStep {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcFileProcessingStep.class);
	
	@Autowired
	private StepBuilderFactory stepBuilders;
	@Autowired
	private PreProcessorParserTasklet tasklet;
	@Value("${preproc.file:input/preproc.csv}")
	private String preprocCsv;
	
	public Step build() {
		LOGGER.debug("Preprocessing file:{}", preprocCsv);
		tasklet.setFile(preprocCsv);
		return stepBuilders.get("step-1:preproc-file-processing").allowStartIfComplete(true).tasklet(tasklet).build();
	}
}
