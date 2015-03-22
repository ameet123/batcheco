package com.att.datalake.locobatch.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.PreProcessingParser;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.locobatch.shared.LocoConfiguration;
import com.att.datalake.locobatch.shared.LocoConfiguration.RuntimeData;
import com.google.gson.Gson;

/**
 * a class to parse pre-processor file as a step task
 * parse the file and store data into the {@link LocoConfiguration} map
 * @author ac2211
 *
 */
@Component
public class PreProcessorParserTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcessorParserTasklet.class);

	private final String STEP_NAME = "step-1:preproc-file-processing";
	private final String STEP_DESCR = "given a file, process it into a schema object";
	@Autowired
	private Gson gson;
	@Autowired
	private PreProcessingParser pr;
	@Autowired
	private LocoConfiguration config;

	public void setFile(String file) {
		pr.setPreProcFile(file);
	}

	@Override
	public String getName() {
		return STEP_NAME;
	}

	@Override
	public String getDescr() {
		return STEP_DESCR;
	}

	@Override
	public void process() {
		List<PreProcSpec> specs = pr.parse();
		for (PreProcSpec p : specs) {
			LOGGER.debug(gson.toJson(p));
			RuntimeData data = config.get(p.getOfferId());
			data.setOfferSpec(p);
		}
	}
}