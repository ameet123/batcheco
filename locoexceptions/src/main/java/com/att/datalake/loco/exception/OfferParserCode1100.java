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
	CRITERION_TYPE_NOT_VALID(1103, "criterion type is not valid, needs to be a letter"), 
	ZERO_OR_INVALID_CRITERION_APPLY_OBJECTS_OR_VALUES(1104, "the columns on which to apply criterion or their values were blank or invalid"),
	NO_PREPROC_FILE_SET(1105,"pre processing file not set"),
	PREPROC_FILE_READ_ERROR(1106,"Could not read pre processing file"),
	PREPROC_STEP_NOT_NUMBER(1107, "pre processing step is not a number"),
	LEFT_TABLE_COLUMNS_NOTEXIST(1108, "no left table columns"),
	PREPROC_MERGE_OP_NOT_VALID(1109, "merge operations are invalid values"),
	RIGHT_TABLE_COLUMNS_NOTEXIST(1110, "no right table columns"),
	PREPROC_STEPS_NOT_IN_ORDER(1111, "pre processing steps should be in sequential order"),
	PREPROC_IN_OUT_NOT_SEQUENTIAL(1112,"output of a step needs to be the input of next for transient tables"),
	PREPROC_OP_COL_NOT_FOUND_IN_SELECT_LIST(1113,"op column for join was not found in the select list of columns"),
	PREPROC_JOIN_TO_UNION_MISMATCH(1114,"no individual sql found for table from union list"),
	PREPROC_OPERATIONS_NOT_COMPLETE(1115,"without a union, there should be only one merge operation, we don't know how to process multiple independent sqls"),
	PREPROC_COL_SPLIT_ERROR(1116, "preprocessing column in left or right table erroneously split, possibly broken on inside comma"),
	PREPROC_MULTIPLE_INSERT_INTO_INVALID(1117, "only one insert into clause is allowed, please check the preproc file specification");

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
