package com.att.datalake.loco.batch.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.util.BatchUtility;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferDataCode1700;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.util.Utility;

/**
 * extract data from final daily table for shipment
 * 
 * @author ac2211
 *
 */
@Component
public class CriteriaSqlExtractor extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaSqlExtractor.class);

	private final String STEP_NAME = "step-30:offer-extraction-runner";

	@Autowired
	private LocoConfiguration config;

	@Qualifier("hiveProcessor")
	@Autowired
	private Processor hp;
	@Autowired
	private SQLClauseBuilder sql;

	@Override
	public String getName() {
		return STEP_NAME;
	}

	@Override
	public void process(ChunkContext context) {
		String criteriaSql = config.getOfferCriteriaSql();
		String localExtractDir = config.getLocalExtractDir();
		sanityCheck(criteriaSql, localExtractDir);

		List<String> sql = new ArrayList<String>();
		sql.add(criteriaSql);
		LOGGER.info("Executing offer criterion SQL \nSQL=>\n{}\n", Utility.prettyPrint(criteriaSql));
		hp.setOutput(BatchUtility.getHiveLogFile(context));
		ProcessorResult pr = hp.run(sql, false);
		printResult(pr);
	}

//	private String prepareExtractSql(String table, String dir) {
//		String stmt = sql.selectAllFrom(table);
//		
//	}

	private void printResult(ProcessorResult pr) {
		String hdr = Utility.pad("*", 80, '*');
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append(hdr + "\n");
		sb.append("\tStatus:" + ((pr.isQuerySuccess()) ? "SUCCESS" : "FAILURE"));
		sb.append("\n\tTable:");
		sb.append(pr.getTable());
		sb.append(" num rows:" + pr.getNumRows());
		sb.append(" num files:" + pr.getNumFiles());
		sb.append("\n" + hdr);
		LOGGER.info(sb.toString());
	}

	private void sanityCheck(String sql, String dir) {
		if (StringUtils.isEmpty(dir) || StringUtils.isEmpty(sql)) {
			throw new LocoException(OfferParserCode1100.CRITERIA_SQL_OR_EXTRACT_DIR_NULL);
		}
	}

}