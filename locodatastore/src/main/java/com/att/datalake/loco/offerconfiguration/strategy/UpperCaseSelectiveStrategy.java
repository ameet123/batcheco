package com.att.datalake.loco.offerconfiguration.strategy;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpperCaseSelectiveStrategy extends DefaultNamingStrategy{
	private static final Logger LOGGER = LoggerFactory.getLogger(UpperCaseSelectiveStrategy.class);
	
	private static final long serialVersionUID = 5494523886596479930L;

	@Override
	public String classToTableName(String className) {
		if (isBatchJobClass(className)) {
			return super.classToTableName(className).toUpperCase();
		}
		return super.classToTableName(className);
	}
	
	private boolean isBatchJobClass(String className) {
		LOGGER.debug("naming strategy incoming Class:{}",className);
		return className.contains("Batch");
	}

}
