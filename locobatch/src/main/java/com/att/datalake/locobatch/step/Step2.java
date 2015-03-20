package com.att.datalake.locobatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreValidationTasklet;

@Component
public class Step2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(Step2.class);

	@Autowired
	private StepBuilderFactory stepBuilders;
	@Autowired
	private PreValidationTasklet tasklet2;

	public Step build() {
		LOGGER.debug("Preprocessing file validation");
		return stepBuilders.get("step-2:preproc-file-validation").allowStartIfComplete(true).tasklet(tasklet2).build();
	}
}