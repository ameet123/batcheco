package com.att.datalake.loco.preproc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;

/**
 * based on the {@link PreProcSpec} generated , generate a list of SQL 
 * for each offer ID
 * @author ac2211
 *
 */
public class PreProcSyntaxGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcSyntaxGenerator.class);

	public Map<String, String> build(List<PreProcSpec> preProcList) {
		Map<String, String> preProcSqlMap = new HashMap<>();
		String offerId;
		for (PreProcSpec o: preProcList) {
			offerId = o.getOfferId();
			LOGGER.info("Generating pre processing SQL for:{}", offerId);
		}
		return preProcSqlMap;
	}
}