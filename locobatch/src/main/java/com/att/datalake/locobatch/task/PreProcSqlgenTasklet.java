package com.att.datalake.locobatch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.builder.AllPreProcSqlBuilder;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.locobatch.shared.LocoConfiguration;
import com.att.datalake.locobatch.shared.LocoConfiguration.RuntimeData;

/**
 * generate sql from {@link PreProcSpec}
 * generate list of {@link PreProcSpec} and pass it to generator
 * pack the result into {@link LocoConfiguration}
 * 
 * @author ac2211
 *
 */
@Component
public class PreProcSqlgenTasklet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcSqlgenTasklet.class);
	@Autowired
	private AllPreProcSqlBuilder preprocsqlBuilder;
	@Autowired
	private LocoConfiguration config;

	@Override
	public RepeatStatus execute(StepContribution contrib, ChunkContext context) throws Exception {
		// generate a list of PreProcSpc to pass to the builder
		List<PreProcSpec> specs = new ArrayList<PreProcSpec>();
		RuntimeData data;
		for (String offer : config.offerIterator()) {
			data = config.get(offer);
			
			if (data.getOfferSpec() != null) {
				LOGGER.debug("Tasklet {}: adding offer {} to sqlgen", this.getClass().getSimpleName(), offer );
				specs.add(data.getOfferSpec());
			}
		}
		
		Map<String, String> sqlMap = preprocsqlBuilder.build(specs);
		
		LOGGER.debug("Generating sql for {} offers, sqls generated:{}", specs.size(), sqlMap.size());
		// save the sql in config map
		for (Entry<String, String> e: sqlMap.entrySet()) {
			data = config.get(e.getKey());
			// pack the sql in runtime data
			data.setPreProcSql(e.getValue());
		}
		// continue further processing
		return RepeatStatus.FINISHED;
	}
}