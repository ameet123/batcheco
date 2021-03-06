package com.att.datalake.loco.mrprocessor.hive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.att.datalake.loco.mrprocessor.utility.StringLoggerStream;

/**
 * provide the mechanism to run hive queries and get hive SQL metadata
 * 
 * @author ac2211
 *
 */
@Component
public class HiveProcessor implements Processor {
	private static final Logger LOGGER = LoggerFactory.getLogger(HiveProcessor.class);

	private Driver driver;
	/**
	 * to store the output of hive query
	 */
	private String outputFile;

	@Autowired
	public HiveProcessor(HiveSession hsession) {
		driver = hsession.getDriver();
	}

	/**
	 * run Hive SQL Query and capture results in a List<String> for later
	 * retrieval we try to catch exceptions and do the markcomplete before
	 * rethrowing them so that the service is freed for the next run.
	 * We need to redirect stderr to capture the hive query logs, so this
	 * method becomes a wrapper
	 * @param command
	 * @return
	 * @throws CommandNeedRetryException
	 * @throws IOException
	 */
	@Override
	public ProcessorResult run(List<String> command, boolean wantResults) {
		StringLoggerStream slErr = StringLoggerStream.create(System.err, outputFile, true);

		// modify error
		System.setErr(slErr);

		// our work
		ProcessorResult pr = runInternal(command, wantResults);

		// restore streams
		System.setErr(slErr.getUnderlying());

		// DEBUG, show what was captured
		packStats(slErr.getCaptured().toString(), pr);
		return pr;
	}

	public void setOutput(String file) {
		this.outputFile = file;
	}
	
	private void packStats(String output, ProcessorResult pr) {
		// capture this : Table db.pqe stats: [numFiles=1, numRows=2,
		// totalSize=1725, rawDataSize=2703]
		Pattern p = Pattern.compile("Table (.*) stats:.*numFiles=([0-9]*), numRows=([0-9]*)");
		Matcher m = p.matcher(output);
		String table = null;
		String localDir;
		int rows = 0, files = 0;
		if (m.find()) {
			table = m.group(1);
			files = Integer.parseInt(m.group(2));
			rows = Integer.parseInt(m.group(3));			
		} else {
			Pattern localDirPattern = Pattern.compile("local directory (.*)$");
			m = localDirPattern.matcher(output);
			if (m.find()) {
				localDir = m.group(1); 
				LOGGER.info("Offer data copied to local directory:{}", localDir);
			} else {
				LOGGER.error("Neither row/file stats nor local directory was found in stdout");
			}
		}
		pr.setNumFiles(files);
		pr.setNumRows(rows);
		pr.setTable(table);
	}

	private ProcessorResult runInternal(List<String> command, boolean wantResults) {
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