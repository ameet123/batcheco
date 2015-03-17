package com.att.datalake.loco.preproc.builder;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.model.PreProcProcessorData;

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
		if (processorDTO.getLeftAlias() != null) {
			processorDTO.getCurrentFromMap().put(processorDTO.getCurrentDetail().getLeftTable(),
					processorDTO.getLeftAlias());
		}
		processorDTO.getCurrentFromMap().put(processorDTO.getCurrentDetail().getRightTable(),
				processorDTO.getRightAlias());
		return processorDTO.getCurrentFromMap();
	}
}