package com.att.datalake.loco.exception;

/**
 * All error codes related to invenio src template
 * 
 * @author am568g
 * 
 */
public enum HiveCode1000 implements ErrorCode {

	METASTORE_ERROR_GETTING_TABLES(1000, "Error getting list of tables by DB using metastore client"), ONLY_ONE_CMD_ALLOWED_IN_HIVE(1001,"run() method takes only one command"),
	METASTORE_CLIENT_STARTUP_ERROR(1002,"could not start up metastore client based on configuration"), HIVE_CLOSE_SESSION_ERROR(1003,"error while closign hive driver and metastore client, probably innocuous.");

	private final int number;
	private final String description;

	private HiveCode1000(int number, String description) {
		this.number = number;
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return number + ": " + description;
	}
}
