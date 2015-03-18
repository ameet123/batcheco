package com.att.datalake.loco.preproc.builder.step;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.model.PreProcProcessorData;

/**
 * build a map of from clauses from the tables passed and the aliases passed
 * 
 * @author ac2211
 *
 */
@Component
public class FromMapBuilder {

	/**
	 * build from clause for the step.
	 * 
	 * @param d
	 * @param fromMap
	 * @param rAlias
	 * @param lAlias
	 * @return
	 */
	public Map<String, String> build(PreProcProcessorData processorDTO) {
		processorDTO.buildFromMap();
		return processorDTO.getCurrentFromMap();
	}
}