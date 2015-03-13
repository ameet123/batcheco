package com.att.datalake.loco.mrprocessor.hive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.CommandNeedRetryException;
import org.apache.hadoop.hive.ql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.HiveCode1000;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;

/**
 * provide the mechanism to run hive queries and get hive SQL metadata
 * 
 * 
 * @author ac2211
 *
 */
@Component
public class HiveProcessor implements Processor {
	private static final Logger LOGGER = LoggerFactory.getLogger(HiveProcessor.class);

	private Driver driver;

	@Autowired
	public HiveProcessor(HiveSession hsession) {
		driver = hsession.getDriver();
	}
	/**
	 * run Hive SQL Query and capture results in a List<String> for later
	 * retrieval we try to catch exceptions and do the markcomplete before
	 * rethrowing them so that the service is freed for the next run.
	 * 
	 * @param command
	 * @return
	 * @throws CommandNeedRetryException
	 * @throws IOException
	 */
	@Override
	public ProcessorResult run(List<String> command, boolean wantResults) {
		if (command.size() != 1) {
			throw new LocoException(HiveCode1000.ONLY_ONE_CMD_ALLOWED_IN_HIVE);
		}
		// create return object to return
		ProcessorResult pr = new ProcessorResult();

		List<String> results = new ArrayList<String>();
		int responseCode = -1;
		try {
			responseCode = driver.run(command.get(0)).getResponseCode();
			if (wantResults) {
				driver.getResults(results);
				pr.setResults(results);
				pr.setRecordCount(results.size());
			}
		} catch (CommandNeedRetryException | IOException e) {
			LOGGER.error(e.getMessage());
		}
		
		pr.setResponseCode(responseCode);
		pr.setQuerySuccess((responseCode == 0) ? true : false);
		return pr;
	}

	@Override
	public void setDbTable(String db, String table) {
		LOGGER.info("not implemented");		
	}
}