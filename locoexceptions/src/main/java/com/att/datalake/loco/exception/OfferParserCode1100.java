package com.att.datalake.loco.exception;

/**
 * All error codes related to invenio src template
 * 
 * @author am568g
 * 
 */
public enum OfferParserCode1100 implements ErrorCode {

	NO_OFFER_FILE_SET(1100, "need to call Offer.setFilename() to set the absolute path of the offer file"),
	OFFER_FILE_READ_ERROR(1101, "could not read offer file"), CRITERION_ID_NOT_A_NUMBER(1102, "criterion id needs to be a number less than 127"),
	CRITERION_TYPE_NOT_VALID(1103, "criterion type is not valid, needs to be a letter"), ZERO_OR_INVALID_CRITERION_APPLY_OBJECTS_OR_VALUES(1104, "the columns on which to apply criterion or their values were blank or invalid");

	private final int number;
	private final String description;

	private OfferParserCode1100(int number, String description) {
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
