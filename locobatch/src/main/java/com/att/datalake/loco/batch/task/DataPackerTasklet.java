package com.att.datalake.loco.batch.task;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.offerconfiguration.model.OfferCriteria;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.att.datalake.loco.util.OfferConstants;
import com.att.datalake.loco.util.Utility;

/**
 * pack data into compressed form if needed and get it ready for shipping
 * 
 * @author ac2211
 *
 */
@Component
public class DataPackerTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataPackerTasklet.class);

	private final String STEP_NAME = "step-31:offer-data-packer";
	@Value("${isCompress:false}")
	private boolean isCompress;

	/**
	 * to verify data loaded in from previous steps
	 */
	@Autowired
	private LocoConfiguration config;
	@Autowired
	private OfferDAO dao;

	@Override
	public String getName() {
		return STEP_NAME;
	}

	/**
	 * the offer criteria SQL is just one for all offers since it contains a
	 * union. This is stored at {@link LocoConfiguration#getCriterionSqls()} and
	 * for the database, there is a separate table {@link OfferCriteria}
	 */
	@Override
	public void process(ChunkContext context) {
		// create ship dir if needed
		Utility.mkdir(OfferConstants.OFFER_SHIP_DIR);

		boolean status;
		// the extract process creates a .crc file, which we want to avoid
		File[] files = Utility.listFiles(config.getLocalExtractDir(), ".");
		status = Utility.concatenateFiles(files, OfferConstants.OFFER_FINAL_FILE);
		// get line count
		int cnt = Utility.getLineCount(OfferConstants.OFFER_FINAL_FILE);
		LOGGER.info("Final output file records # {}", Utility.getLineCount(OfferConstants.OFFER_FINAL_FILE));
		String output = OfferConstants.OFFER_FINAL_FILE;
		if (isCompress) {
			output = OfferConstants.OFFER_FINAL__COMPRESS_FILE;
			status = Utility.compress(new File[] { new File(output) }, OfferConstants.OFFER_FINAL__COMPRESS_FILE);
		}
		if (status) {
			LOGGER.info("Output file:{} with records:{} and size:{} bytes successfully completed", output, cnt,
					new File(output).length());
		} else {
			LOGGER.error("Error in generating the final file");
		}
	}
}