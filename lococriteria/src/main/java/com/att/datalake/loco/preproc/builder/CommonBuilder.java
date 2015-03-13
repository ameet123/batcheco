package com.att.datalake.loco.preproc.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;

/**
 * Generate SQL from {@link PreProcSpec}
 * 
 * @author ac2211
 *
 */
@Component
public class CommonBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonBuilder.class);
	private String START_ALIAS = "`";
	
	
	public String build(PreProcSpec spec) {
		StringBuilder sb = new StringBuilder();
		int prevStep = 0;
		String alias = "a";
		String select, from, table;
		TableClauseBuilder tb;
		// get the details since all the data is in detail
		for (PreProcSpec.ProcDetail d : spec.getProcDetail()) {
			LOGGER.debug("Building pre proc for step:{}", d.getStep());
			if (d.getStep() != prevStep + 1) {
				throw new LocoException(OfferParserCode1100.PREPROC_STEPS_NOT_IN_ORDER);
			}
			table = d.getLeftTable();
			tb = new TableClauseBuilder(table, d.getLeftColumns(), START_ALIAS);
			// make Select clause
			select = tb.getSelect();
			from = tb.getFrom();
			alias = tb.getAlias();
		}

		return sb.toString();
	}
}