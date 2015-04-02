package com.att.datalake.loco.batch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.util.BatchUtility;
import com.att.datalake.loco.mrprocessor.hive.HiveProcessor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;

/**
 * run the pre-processing sql generated using hive
 * iterate over preproc sql from config object and
 * run them using hive.
 * @author ac2211
 *
 */
@Component
public class HiveProcessorTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(HiveProcessorTasklet.class);

	private final String STEP_NAME = "step-20:hive-preproc-sql-runner";

	@Autowired
	private LocoConfiguration config;
	
//	@Qualifier("hiveProcessor")
	@Autowired
	private HiveProcessor hp;
	
	@Override
	public String getName() {
		return STEP_NAME;
	}

	@Override
	public void process(ChunkContext context) {		
		for (Entry<String, String> e: config.getPreProcSqls().entrySet()) {
			List<String> sql = new ArrayList<String>();
			sql.add(e.getValue());
			LOGGER.info("Processing pre-requisite SQL for offer:{}", e.getKey());
			hp.setOutputFile(BatchUtility.getHiveLogFile(context));
			ProcessorResult pr = hp.run(sql, false);
			LOGGER.info("Processing pre-requisite SQL for offer:{} success?:{}", e.getKey(), pr.isQuerySuccess());
		}
	}
}