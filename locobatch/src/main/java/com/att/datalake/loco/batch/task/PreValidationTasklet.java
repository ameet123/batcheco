package com.att.datalake.loco.batch.task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.shared.LocoConfiguration.RuntimeData;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.preproc.model.PreProcSpec.ProcDetail;

/**
 * validate the data parsed in previous step
 * 
 * @author ac2211
 *
 */
@Component
public class PreValidationTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreValidationTasklet.class);

	private final String STEP_NAME = "step-12:preproc-file-validation";

	@Autowired
	private LocoConfiguration config;

	private Pattern columnValidationPattern;

	public PreValidationTasklet() {
		columnValidationPattern = Pattern.compile("(^[^\\(]+|(?<=\\()[^\\)]+(?=\\)))");
	}

	@Override
	public String getName() {
		return STEP_NAME;
	}
	/**
	 * fetch from {@link LocoConfiguration} and loop over all columns parsed, in
	 * turn validating them
	 */
	@Override
	public void process(ChunkContext context) {
		for (String offer : config.offerIterator()) {
			// get runtimedata
			RuntimeData d = config.get(offer);
			for (ProcDetail detail : d.getOfferSpec().getProcDetail()) {
				validate(detail.getLeftColumns(), offer);
				validate(detail.getRightColumns(), offer);
			}
			LOGGER.debug("Offer:{} validated", offer);
		}
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