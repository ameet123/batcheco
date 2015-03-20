package com.att.datalake.locobatch.task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.preproc.model.PreProcSpec.ProcDetail;
import com.att.datalake.locobatch.shared.LocoConfiguration;
import com.att.datalake.locobatch.shared.LocoConfiguration.RuntimeData;

/**
 * validate the data parsed in previous step
 * 
 * @author ac2211
 *
 */
@Component
public class PreValidationTasklet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreValidationTasklet.class);

	@Autowired
	private LocoConfiguration config;

	private Pattern columnValidationPattern;

	public PreValidationTasklet() {
		columnValidationPattern = Pattern.compile("(^[^\\(]+|(?<=\\()[^\\)]+(?=\\)))");
	}

	/**
	 * fetch from {@link LocoConfiguration} and loop over all columns parsed, in
	 * turn validating them
	 */
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		for (String offer : config.offerIterator()) {
			// get runtimedata
			RuntimeData d = config.get(offer);
			for (ProcDetail detail : d.getOfferSpec().getProcDetail()) {
				validate(detail.getLeftColumns(), offer);
				validate(detail.getRightColumns(), offer);
			}
			LOGGER.debug("Offer:{} validated", offer);
		}
		return RepeatStatus.FINISHED;
	}

	private void validate(List<String> columns, String offer) {
		Matcher m;
		for (String c : columns) {
			m = columnValidationPattern.matcher(c);
			if (!m.find()) {
				throw new LocoException(OfferParserCode1100.PREPROC_COL_SPLIT_ERROR).set("offer", offer).set("col", c);
			}
		}
	}
}
