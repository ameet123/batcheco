package com.att.datalake.loco.preproc.builder.step;

import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.model.PreProcOperation;
import com.att.datalake.loco.offercriteria.model.PreProcProcessorData;
import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.util.OfferParserUtil;

/**
 * preprocessing steps required to set the variables in
 * {@link PreProcProcessorData} object are done here. This essentially "adjusts"
 * the object to the step we are currently processing
 * 
 * @author ac2211
 *
 */
@Component
public class StepCalibrator {

	/**
	 * this is done for processing each step of the list of steps in
	 * {@link ProcDetail} this moves the "counter" through the steps, so to
	 * speak
	 * 
	 * if a new group is started, then get a new map, otherwise get the old one
	 * The old one matches the previous output table/file Do this for all
	 * relavant clauses - select, from ...
	 * 
	 * @param processorDTO
	 * @param d
	 */
	public void calibrate(PreProcProcessorData processorDTO, PreProcSpec.ProcDetail d) {
		processorDTO.setCurrentDetail(d);

		if (d.getOp() == PreProcOperation.JOIN.getValue()) {
			if (isNewGroup(processorDTO.getPrevOutput(), d.getLeftTable(), processorDTO.getPrevOp(), d.getOp())) {
				processorDTO.addNewMapEntries(d.getOutput());
			} else {
				processorDTO.updateMapEntries(d.getOutput());
			}

			setTableLevelData(processorDTO, d);
		}
	}

	/**
	 * adjusts the alias and column data as appropriate for passing to client
	 * 
	 * @param d
	 */
	private void setTableLevelData(PreProcProcessorData processorDTO, PreProcSpec.ProcDetail d) {
		// process left if needed
		if (!OfferParserUtil.isTransient(d.getLeftTable())) {
			processorDTO.setLeftColumns(d.getLeftColumns());
			processorDTO.incrementLeftAlias();
		} else {
			processorDTO.setLeftColumns(null);
			processorDTO.nullifyLeftAlias();
		}
		if (!OfferParserUtil.isTransient(d.getRightTable())) {
			processorDTO.setRightColumns(d.getRightColumns());
			processorDTO.incrementRightAlias();
		} else {
			processorDTO.setRightColumns(null);
			processorDTO.nullifyRightAlias();
		}
		processorDTO.updateAliasMap();
	}

	/**
	 * if the current left table is different from the prev output OR prevOP is
	 * diff from currentOp(from join to union) then we need to start a new
	 * "group" for the operations as such, we need to generate new storage
	 * structures - maps for select etc.
	 * 
	 * @param prevOutput
	 * @param currentLeft
	 * @param prevOp
	 * @param currentOp
	 * @return
	 */
	private boolean isNewGroup(String prevOutput, String currentLeft, char prevOp, char currentOp) {
		if (prevOutput == null || !prevOutput.equals(currentLeft) || prevOp != currentOp) {
			return true;
		}
		return false;
	}
}