package com.att.datalake.locobatch.task;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PreValidationTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreValidationTasklet.class);

	private final String STEP_NAME = "step-2:preproc-file-validation";
	private final String STEP_DESCR = "after processing the preprocessing syntax file, validate the parsing into object, ensure that columns are not broken into unintentional fragments";

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

	@Override
	public String getDescr() {
		return STEP_DESCR;
	}

	/**
	 * fetch from {@link LocoConfiguration} and loop over all columns parsed, in
	 * turn validating them
	 */
	@Override
	public void process() {
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