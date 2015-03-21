package com.att.datalake.locobatch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.locobatch.task.PreProcSqlgenTasklet;

/**
 * for sql generation
 * 
 * @author ac2211
 *
 */
@Component
public class Step3 {
	private static final Logger LOGGER = LoggerFactory.getLogger(Step3.class);

	private final String STEP_NAME = "step-3:preproc-sql-generation";
	@Autowired
	private StepBuilderFactory stepBuilders;
	@Autowired
	private PreProcSqlgenTasklet tasklet3;

	public Step build() {
		LOGGER.debug("Preprocessing sql generation");
		return stepBuilders.get(STEP_NAME).allowStartIfComplete(true).tasklet(tasklet3).build();
	}
}