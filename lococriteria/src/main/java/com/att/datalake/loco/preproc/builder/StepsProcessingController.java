package com.att.datalake.loco.preproc.builder;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.builder.step.FromMapBuilder;
import com.att.datalake.loco.preproc.builder.step.PredicateBuilder;
import com.att.datalake.loco.preproc.builder.step.SelectColMapBuilder;
import com.att.datalake.loco.preproc.builder.step.SqlFromComponentBuilder;
import com.att.datalake.loco.preproc.builder.step.StepCalibrator;
import com.att.datalake.loco.preproc.builder.step.StepValidator;
import com.att.datalake.loco.preproc.model.PreProcOperation;
import com.att.datalake.loco.preproc.model.PreProcProcessorData;
import com.att.datalake.loco.preproc.model.PreProcSpec;

/**
 * a class where based on list of columns and a table name, we can generate from
 * and select clauses we accept the details object and iterate over it applying
 * all the logic of how to construct the final SQL. This would involve checking
 * for "join" and "union" operations matching the output of one step to the
 * input of the next
 * 
 * @author ac2211
 *
 */
@Component
public class StepsProcessingController {
	private static final Logger LOGGER = LoggerFactory.getLogger(StepsProcessingController.class);

	@Autowired
	private SelectColMapBuilder selectBuilder;
	@Autowired
	private PredicateBuilder predicateBuilder;
	@Autowired
	private FromMapBuilder fromBuilder;
	@Autowired
	private SqlFromComponentBuilder wholeBuilder;
	@Autowired
	private StepValidator validator;
	@Autowired
	private StepCalibrator calibrator;

	/**
	 * we iterate over all the records and group them in the order they appear
	 * for specific processing. we collect all join operations and we collect
	 * all the union operations 0. initialize previous step variables 1. iterate
	 * over steps/details 2. for each, validate the step record 3. process join
	 * or union as appropriate
	 * 
	 */
	public String build(List<PreProcSpec.ProcDetail> details) {
		PreProcProcessorData processorDTO = new PreProcProcessorData();

		// iterate over the list of steps
		for (PreProcSpec.ProcDetail d : details) {
			calibrator.calibrate(processorDTO, d);

			// validation of data
			validator.validate(processorDTO);

			if (d.getOp() == PreProcOperation.JOIN.getValue()) {
				processJoinStep(processorDTO);
			} else if (d.getOp() == PreProcOperation.UNION.getValue()) {
				processUnionStep(processorDTO);
			}
			processorDTO.setStepCompletion();
		}

		// out of the processing loop for all steps, we have the requisite maps
		// built  to construct the sql, 
		// we can print the maps for debug, if desired
		if (LOGGER.isTraceEnabled()) {
			processorDTO.debugPrint();
		}
		return wholeBuilder.build(processorDTO);
	}

	private void processJoinStep(PreProcProcessorData processorDTO) {
		selectBuilder.build(processorDTO);
		fromBuilder.build(processorDTO);
		predicateBuilder.build(processorDTO);
	}
	private void processUnionStep(PreProcProcessorData processorDTO) {
		processorDTO.processUnion();
	}
}