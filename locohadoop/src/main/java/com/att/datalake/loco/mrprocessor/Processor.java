package com.att.datalake.loco.mrprocessor;

import java.util.List;

import com.att.datalake.loco.mrprocessor.model.ProcessorResult;


/**
 * define a common interface for m/r processing
 * @author ac2211
 *
 */
public interface Processor {
	/**
	 * run a list of commands using the appropriate processor
	 * @param command
	 * @param wantResults
	 * @return
	 */
	ProcessorResult run(List<String> command, boolean wantResults);
	/**
	 * for metadata processing, we need to know the db and the table
	 * @param db
	 * @param table
	 */
	void setDbTable(String db, String table);
}