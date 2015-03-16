package com.att.datalake.loco.preproc.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;

/**
 * Generate SQL from {@link PreProcSpec}
 * 
 * @author ac2211
 *
 */
@Component
public class CommonBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonBuilder.class);
	@Autowired
	TableClauseBuilder tb;
	public void build(PreProcSpec spec) {

		tb.build(spec.getProcDetail());
		// get the details since all the data is in detail
		
	}
}