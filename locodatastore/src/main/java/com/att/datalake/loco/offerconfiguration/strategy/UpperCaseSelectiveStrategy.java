package com.att.datalake.loco.offerconfiguration.strategy;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * this is to handle upper case table names from spring batch
 * @author ac2211
 *
 */
public class UpperCaseSelectiveStrategy extends ImprovedNamingStrategy{
	private static final Logger LOGGER = LoggerFactory.getLogger(UpperCaseSelectiveStrategy.class);
	
	private static final long serialVersionUID = 5494523886596479930L;

	/**
	 * we just override the classToTables method
	 * @param className this is actually the entity name specified in the class
	 * so for BATCH_JOB_EXECUTION, the name comes in already upper case, 
	 * but then super.classToTableName turns it to lower case,
	 * so that's where we intercept and do upper case
	 */
	@Override
	public String classToTableName(String className) {
		if (isBatchJobClass(className)) {
			LOGGER.debug("table name TO BE REturned, after upper casing:{}",super.classToTableName(className).toUpperCase());
			return super.classToTableName(className).toUpperCase();
		}
		return super.classToTableName(className);
	}
	
	private boolean isBatchJobClass(String className) {
		LOGGER.debug("naming strategy incoming Class:{}",className);
		return className.startsWith("BATCH");
	}

}
