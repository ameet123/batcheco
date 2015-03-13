package com.att.datalake.loco.exception;

/**
 * All error codes related to spring integration context startup
 * 
 * @author am568g
 * 
 */
public enum SIContextCode1500 implements ErrorCode {

	NO_DATA_DIR_SUPPLIED(1500,"Top level data directory needs to be passed as a JVM argument -Ddata.dir=<>"),
	NO_OFFER_CRITERIA_FILE_SUPPLIED(1500,"Offer criteria CSV file needs to be passed as a JVM argument -Doffer.file=<>");

	private final int number;
	private final String description;

	private SIContextCode1500(int number, String description) {
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
