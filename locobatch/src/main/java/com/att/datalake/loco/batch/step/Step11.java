package com.att.datalake.loco.batch.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.batch.task.PreProcessorParserTasklet;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;

@Component
public class Step11 extends AbstractLocoStep {
	private static final Logger LOGGER = LoggerFactory.getLogger(Step11.class);
	
	private final String stepName = "step-11:preproc-file-processing";
	private final String stepDescr = "given a file, process it into a schema object";

	@Autowired
	private PreProcessorParserTasklet tasklet11;

	@Value("${preproc.file}")
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
		if (StringUtils.isEmpty(preprocCsv)) {
			throw new LocoException(OfferParserCode1100.NO_PREPROC_FILE_SET);
		}
		LOGGER.info("Pre-processing file:{}", preprocCsv);
		tasklet11.setFile(preprocCsv);
		return tasklet11;
	}
}
