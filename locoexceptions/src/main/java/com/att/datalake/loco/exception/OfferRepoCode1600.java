package com.att.datalake.loco.exception;

/**
 * All error codes related to spring integration context startup
 * 
 * @author am568g
 * 
 */
public enum OfferRepoCode1600 implements ErrorCode {

	LOCAL_DIR_NOT_VALID(1600,"please supply non-null and valid string dir name"),
	OFFER_ID_NOT_VALID(1601,"please supply non-null and valid string offer ID name"),
	OFFER_TO_SAVE_IS_NULL(1602,"Cannot save null offer");

	private final int number;
	private final String description;

	private OfferRepoCode1600(int number, String description) {
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
