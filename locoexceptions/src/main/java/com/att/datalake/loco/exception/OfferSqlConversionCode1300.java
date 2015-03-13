package com.att.datalake.loco.exception;

/**
 * All error codes related to invenio src template
 * 
 * @author am568g
 * 
 */
public enum OfferSqlConversionCode1300 implements ErrorCode {

	MULTIPLE_COLS_FOR_EXCLUSION(1300,"Exclusionary criterion can be applied to only a single column"),
	AGGREGATE_FUN_SPEC_ERROR(1301, "Aggregate function needs to be <FUN>(<COL>) e.g. SUM(colA)"),
	ROUNDING_COL_ALIAS_NUMBER_MISMATCH(1302,"For rounding, the transform count should match alias count"),
	ROUND_TRANSFORM_ON_AGGREGATE_COL_ERROR(1303,"aggregate column did not match , but was expected");
	
	
	private final int number;
	private final String description;

	private OfferSqlConversionCode1300(int number, String description) {
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
