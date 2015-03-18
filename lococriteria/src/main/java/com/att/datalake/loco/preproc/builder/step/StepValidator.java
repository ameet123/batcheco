package com.att.datalake.loco.preproc.builder.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.preproc.model.PreProcProcessorData;
import com.att.datalake.loco.util.OfferParserUtil;

/**
 * validate each step of the preprocessing detail object
 */
@Component
public class StepValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(StepValidator.class);

	/**
	 * if the current file is first entry or is not a transient table, i.e.
	 * starting with FILE then just return, all izz well
	 * @param processorDTO
	 */
	public void validate(PreProcProcessorData processorDTO) {
		LOGGER.debug("Validating step:{}", processorDTO.getCurrentDetail().getStep());
		if ((processorDTO.getPrevStep() + 1) != processorDTO.getCurrentDetail().getStep()) {
			throw new LocoException(OfferParserCode1100.PREPROC_STEPS_NOT_IN_ORDER);
		}
		if (StringUtils.isEmpty(processorDTO.getPrevOutput())
				|| !OfferParserUtil.isTransient(processorDTO.getCurrentDetail().getLeftTable())) {
			return;
		}
		LOGGER.debug("Checking output:{} current left:{}", processorDTO.getPrevOutput(), processorDTO
				.getCurrentDetail().getLeftTable());
		if (!processorDTO.getPrevOutput().equals(processorDTO.getCurrentDetail().getLeftTable())) {
			throw new LocoException(OfferParserCode1100.PREPROC_IN_OUT_NOT_SEQUENTIAL);
		}
	}
}
