package com.att.datalake.loco.preproc.model;

/**
 * type of valid operations allowed
 * 
 * @author ac2211
 *
 */
public enum PreProcOperation {

	JOIN('M'), UNION('C'), INSERT('I');
	private char value;

	private PreProcOperation(char value) {
		this.value = value;
	}

	public char getValue() {
		return value;
	}
}
