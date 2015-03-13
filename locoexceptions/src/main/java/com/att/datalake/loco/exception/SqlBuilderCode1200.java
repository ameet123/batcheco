package com.att.datalake.loco.exception;

/**
 * All error codes related to invenio src template
 * 
 * @author am568g
 * 
 */
public enum SqlBuilderCode1200 implements ErrorCode {

	NO_SELECT_CLAUSE_SET(1200,"select clause is mandatory"), NO_FROM_CLAUSE_SET(1201,"FROM clause is mandatory"), NO_GROUPBY_CLAUSE_SET(1202,"GROUP BY clause is required in this case");
	
	private final int number;
	private final String description;

	private SqlBuilderCode1200(int number, String description) {
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
