package com.att.datalake.loco.batch.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.util.BatchUtility;
import com.att.datalake.loco.exception.HiveCode1000;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
import com.att.datalake.loco.util.Utility;

/**
 * run the pre-processing sql generated using hive
 * iterate over preproc sql from config object and
 * run them using hive.
 * @author ac2211
 *
 */
@Component
public class PreProcSqlRunnerTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcSqlRunnerTasklet.class);

	private final String STEP_NAME = "step-20:hive-preproc-sql-runner";

	@Autowired
	private LocoConfiguration config;
	
	@Qualifier("hiveProcessor")
	@Autowired
	private Processor hp;
	
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
			hp.setOutput(BatchUtility.getHiveLogFile(context));
			ProcessorResult pr = hp.run(sql, false);
			if (!pr.isQuerySuccess()) {
				throw new LocoException("Pre proc sql failed", HiveCode1000.HIVE_DRIVER_QUERY_FAILED);
			}
			printResult(pr);
		}
	}
	private void printResult(ProcessorResult pr) {
		String hdr = Utility.pad("*", 80, '*');
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(hdr+"\n");
		sb.append("\tStatus:"+ ((pr.isQuerySuccess())?"SUCCESS":"FAILURE"));
		sb.append("\n\tTable:");
		sb.append(pr.getTable());
		sb.append(" num rows:"+pr.getNumRows());
		sb.append(" num files:"+pr.getNumFiles());
		sb.append("\n"+hdr);
		LOGGER.info(sb.toString());
	}
}