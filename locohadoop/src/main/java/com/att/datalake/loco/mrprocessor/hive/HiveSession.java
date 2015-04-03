package com.att.datalake.loco.mrprocessor.hive;

import java.util.List;

import javax.annotation.PreDestroy;

import org.apache.hadoop.hive.cli.CliSessionState;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.ql.Driver;
import org.apache.hadoop.hive.ql.MapRedStats;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.HiveCode1000;
import com.att.datalake.loco.exception.LocoException;

/**
 * this starts the hive session so that we can spawn hive driver and metastore
 * client programs The reason it is separated out is because we desire to
 * separate out {@link HiveProcessor} and {@link HiveMetaProcessor} and both
 * need the session. Since we are programming to interaface and not
 * implementation, we are not able to get fields/methods at runtime IMP:
 * According to hive.ql.Driver code
 * 
 * @see <a
 *      href="http://grepcode.com/file/repo1.maven.org/maven2/org.apache.hive/hive-exec/0.13.0/org/apache/hadoop/hive/ql/Driver.java#Driver.runInternal%28java.lang.String%2Cboolean%29">Driver</a>
 *      Driver is capable of running multiple queries via a transaction manager.
 *      The context is gotten in a synchronized fashion allowing for concurrency
 * 
 *      This will be injected by {@link HiveProcessor} and
 *      {@link HiveMetaProcessor}
 * 
 * @author ac2211
 *
 */
@Component
public class HiveSession {
	private static final Logger LOGGER = LoggerFactory.getLogger(HiveSession.class);

	@Value("${mr.output.silent:false}")
	private boolean mrSilent;
	
	private SessionState state;

	private HiveConf hiveConf = null;
	private Driver driver = null;
	protected HiveMetaStoreClient client = null;

	public HiveSession() {
		setUpHiveConf();
		driver = new Driver(hiveConf);
		try {
			client = new HiveMetaStoreClient(hiveConf);
		} catch (MetaException e) {
			throw new LocoException(e, HiveCode1000.METASTORE_CLIENT_STARTUP_ERROR);
		}
		state = SessionState.start(new CliSessionState(hiveConf));
	}
	public List<MapRedStats> getStats() {
		return state.getLastMapRedStatsList();
	}

	/**
	 * perform initialization, any variables needed can be added to
	 * configuration here these variables can then be used inside SQL script
	 * Note: Tried this to get guava new version to load - no dice
	 * hiveConf.setBoolean("mapreduce.task.classpath.user.precedence", true);
	 * hiveConf.setBoolean("mapreduce.user.classpath.first", true);
	 */
	private void setUpHiveConf() {
		hiveConf = new HiveConf();
		if (mrSilent) {
			hiveConf.setBoolVar(HiveConf.ConfVars.HIVESESSIONSILENT, true);
		}
	}

	/**
	 * @return the driver
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * @return the client
	 */
	public HiveMetaStoreClient getClient() {
		return client;
	}

	/**
	 * close connection and session
	 */
	@PreDestroy
	public void close() {
		LOGGER.info("Tearing down Hive Session at context shutdown ...");
		try {
			driver.destroy();
			driver.close();
			client.close();
		} catch (Exception e) {
			throw new LocoException(e, HiveCode1000.HIVE_CLOSE_SESSION_ERROR);
		}
	}
}