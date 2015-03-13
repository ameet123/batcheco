package com.att.datalake.loco.exception;

/**
 * All error codes related to offer data files
 * 
 */
public enum IngestionCode1800 implements ErrorCode {

	ERROR_IN_MOVING_FILE_TO_HDFS(1800,"either local file did not exist or there was error in moving"),
	LOCA_FILE_MISSING(1801,"local file missing after the flow was kick started, strange");

	private final int number;
	private final String description;

	private IngestionCode1800(int number, String description) {
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
