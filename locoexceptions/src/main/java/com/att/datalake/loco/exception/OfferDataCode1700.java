package com.att.datalake.loco.exception;

/**
 * All error codes related to offer data files
 * 
 */
public enum OfferDataCode1700 implements ErrorCode {

	FILE_NOT_MATCH_COMPNENT(1700,"file directory does not match a registered offer component"),
	NO_MATCHING_OFFER_FOR_COMPNENT(1701,"should not happen due to FK, but offer was probably null for a given component or lazy initialization"),
	OFFER_TABLE_MAP_FILE_ERROR(1702,"offer_table_map.yml seems to contain invalid data"),
	OFFER_TABLE_INVALID_DATA(1703, "the size of offer->table map is 0");

	private final int number;
	private final String description;

	private OfferDataCode1700(int number, String description) {
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
