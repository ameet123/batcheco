package com.att.datalake.loco.mrprocessor.pig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.executionengine.ExecJob;
import org.apache.pig.data.Tuple;
import org.apache.pig.tools.pigstats.PigStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.hadoop.pig.PigTemplate;
import org.springframework.stereotype.Service;

import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;

/**
 * offer pig processing in generic way. This is not thread-safe, as such we want
 * to allow only one query to run at a time.
 * Usage: 
 * PIG Transform to copy from source to COPY
		List<String> queries = new ArrayList<String>();
		queries.add("A = LOAD 'ameet.my_salary' using org.apache.hcatalog.pig.HCatLoader() as (id,name, salary);");
		queries.add("B = FOREACH A GENERATE id, name, salary*100 as salary;");
		queries.add("store B into 'ameet.my_salary_copy' using org.apache.hcatalog.pig.HCatStorer();");
		pr = pigProcessor.run(queries, false);
		System.out.println("Success:" + pr.isQuerySuccess() + " Recordcnt=>" + pr.getRecordCount());
 * 
 * @author ac2211
 *
 */
@Service
public class PigProcessor implements Processor{
	private static final Logger LOGGER = LoggerFactory.getLogger(PigProcessor.class);

	@Autowired
	private PigTemplate pigTemplate;
	@Value("${loader.path}")
	private String[] classpath;
	private List<ExecJob> status;

	/**
	 * execute generic query. we first ensure that only one thread can run a
	 * query by initializaing the atomic boolean Since this really meant for
	 * running map/reduce jobs, we will not return anything out of this. i.e. no
	 * results will be returned
	 * 
	 * @param queries This is a List<String> , each item is a pig line
	 * @return {@link ProcessorResult}
	 */
	public synchronized ProcessorResult run(List<String> queries, boolean wantResults) {		
		ProcessorResult pr = new ProcessorResult();
		Iterator<Tuple> results;

		String jobName = "Loco_PigProcessor_Query_"+System.currentTimeMillis();
			pigTemplate.execute((PigServer pig) -> {
				pig.setBatchOn();
				pig.setJobName(jobName);
				registerJars(pig);
				for (String q : queries) {
					pig.registerQuery(q);
				}
				status = pig.executeBatch();
				return null;
			});

		for (ExecJob j : status) {
			if (j.getStatus().compareTo(ExecJob.JOB_STATUS.COMPLETED) != 0) {
				pr.setQuerySuccess(false);
				LOGGER.error("Pig job:{} error:{}", j.getException());
				break;
			}
			PigStats stats = j.getStatistics();
			pr.setResponseCode(stats.getReturnCode());
			LOGGER.info("status:{} Records Written:{} Duration:{}", j.getStatus().name(), stats.getRecordWritten(),
					stats.getDuration());
			pr.setRecordCount((int) stats.getRecordWritten());
			pr.setQuerySuccess(true);
			if (wantResults) {
				try {
					results = j.getResults();
					setResults(pr, results);
				} catch (ExecException e) {
					LOGGER.error("Error in fetching results in Pig job:{} error:{}", jobName, e.getMessage());
				}
			}
		}
		return pr;
	}

	private void setResults(ProcessorResult pr, Iterator<Tuple> results) {
		List<String> stringResults = new ArrayList<String>();
		Tuple t;
		StringBuilder sb;
		while (results.hasNext()){
			t = results.next();
			sb = new StringBuilder();
			// collect the results in string format
			for (Object o: t.getAll()) {
				sb.append((String)o);
				sb.append("|");
			}
			sb.setLength(sb.length()-1);
			stringResults.add(sb.toString());
		}
		pr.setResults(stringResults);
	}

	private void registerJars(PigServer pig) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("registering => ");
		for (String s : classpath) {			
			if (s.matches(".*hive-hcatalog-core.*jar|.*hive-exec.*jar|.*hive-metastore.*jar|.*pig.*core-h2.jar|.*fb303.*jar")) {
				sb.append(s+", ");
				pig.registerJar(s);
				continue;
			}
			if (s.matches(".*jars/?")) {
				File jars = new File(s);				
				for (File f : jars.listFiles()) {
					sb.append( f.getCanonicalPath()+", ");
					pig.registerJar(f.getCanonicalPath());
				}
			}
		}
		sb.setLength(sb.length() - 2);
		LOGGER.debug(sb.toString());
	}

	@Override
	public void setDbTable(String db, String table) {
		LOGGER.info("not implemented");
		
	}
}