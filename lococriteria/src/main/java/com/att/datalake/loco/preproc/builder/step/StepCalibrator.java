package com.att.datalake.loco.preproc.builder.step;

import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.model.PreProcOperation;
import com.att.datalake.loco.preproc.model.PreProcProcessorData;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.loco.preproc.model.PreProcSpec.ProcDetail;

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
			processorDTO.calibrateTabular(d);
		}
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