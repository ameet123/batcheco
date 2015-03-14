package com.att.datalake.loco.preproc.builder;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
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
	
	public void build(PreProcSpec spec) {

		int prevStep = 0;
		TableClauseBuilder tb = new TableClauseBuilder();
		// get the details since all the data is in detail
		for (PreProcSpec.ProcDetail d : spec.getProcDetail()) {
			LOGGER.debug("Building pre proc for step:{}", d.getStep());
			if (d.getStep() != prevStep + 1) {
				throw new LocoException(OfferParserCode1100.PREPROC_STEPS_NOT_IN_ORDER);
			}		
			tb.addStep(d);
			prevStep++;
		}
		print(tb);
		
	}
	private void print(TableClauseBuilder tb) {
		System.out.println("Items:"+tb.getSelectMap().size());
		for (Entry<String, String> e: tb.getSelectMap().entrySet()) {
			System.out.println("key=>"+e.getKey()+" val=>"+e.getValue());
		}
	}
}