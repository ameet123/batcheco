package com.att.datalake.loco.offercriteria.model;
/**
 * type of valid operations allowed
 * @author ac2211
 *
 */
public enum PreProcOperation {

	JOIN('M'), UNION('C');
	private char value;

	private PreProcOperation(char value) {
		this.value = value;
	}

	public char getValue() {
		return value;
	}
}
