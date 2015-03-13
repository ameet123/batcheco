package com.att.datalake.loco.datashipping;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
import com.att.datalake.loco.util.ExtractConstants;
import com.att.datalake.loco.util.OfferConstants;
import com.att.datalake.loco.util.Utility;

/**
 * prepare the data for transmitting this will require extraction as well as
 * final packaging using compression We are assuming that we will store the data
 * in ORC format and compressed, as such an m/r job is required to extract to
 * CSV format
 * 
 * @author ac2211
 *
 */
@Component
public class ExtractOfferPoints {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractOfferPoints.class);
	private List<String> extractSQL;
	@Resource
	@Qualifier("hiveProcessor")
	private Processor hp;

	/**
	 * prepare directories the first time
	 */
	public ExtractOfferPoints() {
		if (Utility.mkdir(ExtractConstants.OUTPUT_DATA_LANDING_DIR)) {
			LOGGER.info("Output landing zone dir created successfully:{}", ExtractConstants.OUTPUT_DATA_LANDING_DIR);
		}
		prepareExtractSql();
	}

	/**
	 * extract from hive table via m/r
	 * 
	 * @return query status result
	 */
	public boolean get() {
		ProcessorResult pr = hp.run(extractSQL, false);
		LOGGER.info("Extract query result:{} success? {}", pr.getResponseCode(), pr.isQuerySuccess());
		return pr.isQuerySuccess();
	}

	private void prepareExtractSql() {
		extractSQL = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OVERWRITE LOCAL DIRECTORY '");
		sb.append(ExtractConstants.OUTPUT_DATA_LANDING_DIR);
		sb.append("' ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' SELECT * FROM ");
		sb.append(OfferConstants.OFFER_FINAL_TABLE);
		extractSQL.add(sb.toString());
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Extract SQL prepared:{}", sb.toString());
		}
	}
}