package com.att.datalake.loco.mrprocessor.hive;

import java.util.List;

import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.HiveCode1000;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
/**
 * this manages metadata interactions with hive tables -all DDLs
 * Usage:
 * commands = new ArrayList<String>();
 * commands.add("truncate");
 * hiveMetaProcessor.setDbTable("ameet", "my_salary_copy");
 * pr = hiveMetaProcessor.run(commands, false);
 * System.out.println("Truncate command result:" + pr.isQuerySuccess());
 * 
 * @author ac2211
 *
 */
@Component
public class HiveMetaProcessor implements Processor {
	private static final Logger LOGGER = LoggerFactory.getLogger(HiveMetaProcessor.class);
	
	private HiveMetaStoreClient client;
	private String db;
	private String table;
	
	@Autowired
	public HiveMetaProcessor(HiveSession hsession) {
		this.client = hsession.getClient();
	}

	public void setDbTable(String db, String table) {
		this.db = db;
		this.table = table;
	}

	@Override
	public ProcessorResult run(List<String> command, boolean wantResults) {
		if (command.size() != 1) {
			throw new LocoException(HiveCode1000.ONLY_ONE_CMD_ALLOWED_IN_HIVE);
		}
		String cmd = command.get(0);
		ProcessorResult pr = new ProcessorResult();
		if (db == null) {
			return pr;
		}
		switch (cmd) {
		case "truncate":
			try {
				if (table == null) {
					return pr;
				}
				truncateTable(db, table);
				// if we are here, => success
				pr.setQuerySuccess(true);
			} catch (TException e) {
				LOGGER.error("error performing truncate:{}", e.getMessage());
			}
			break;
		case "getTables":
			try {
				List<String> results = getTables(db);
				pr.setResults(results);
				pr.setQuerySuccess(true);
				pr.setRecordCount(results.size());
			} catch (LocoException e) {
				LOGGER.error("code:{} msg:{}",e.getErrorCode(), e.getErrorCode().getDescription());
			}
		default:
			break;
		}
		return pr;
	}
	/**
	 * strange but it seems that you cannot truncate a table from non-default
	 * database via driver.
	 * 
	 * @param db
	 * @param table
	 * @throws MetaException
	 * @throws NoSuchObjectException
	 * @throws TException
	 */
	private void truncateTable(String db, String table) throws TException {
		Table t = client.getTable(db, table);
		client.dropTable(db, table);
		client.createTable(t);
	}
	/**
	 * get a list of tables by database
	 * 
	 * @param db
	 * @return List<String> list of tables
	 */
	private List<String> getTables(String db) {
		try {
			return client.getAllTables(db);
		} catch (MetaException e) {
			throw new LocoException(e, HiveCode1000.METASTORE_ERROR_GETTING_TABLES);
		}
	}
}