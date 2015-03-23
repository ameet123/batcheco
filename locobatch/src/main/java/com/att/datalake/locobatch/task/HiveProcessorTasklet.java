package com.att.datalake.locobatch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
import com.att.datalake.locobatch.shared.LocoConfiguration;

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
	@Resource
	@Qualifier("hiveProcessor")
	private Processor hp;
	
	@Override
	public String getName() {
		return STEP_NAME;
	}

	@Override
	public void process() {		
		for (Entry<String, String> e: config.getPreProcSqls().entrySet()) {
			List<String> sql = new ArrayList<String>();
			sql.add(e.getValue());
			LOGGER.info("Processing pre-requisite SQL for offer:{}", e.getKey());
			ProcessorResult pr = hp.run(sql, false);
			LOGGER.info("Processing pre-requisite SQL for offer:{} success?:{}", e.getKey(), pr.isQuerySuccess());
		}
	}
}