package com.att.datalake.loco.mrprocessor.model;

import java.util.List;

/**
 * a data structure class to receive the results of a processor query
 * and pack in relevant attributes in
 * @author ac2211
 *
 */
public class ProcessorResult {
	private int responseCode;
	private List<String> results;
	private boolean querySuccess;
	private int recordCount;
	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the results
	 */
	public List<String> getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(List<String> results) {
		this.results = results;
	}
	/**
	 * we have to receive all the results and only then count, so it is expensive
	 * @return the recordCount
	 */
	public int getRecordCount() {
		return recordCount;
	}
	/**
	 * @param recordCount the recordCount to set
	 */
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	/**
	 * @return the querySuccess
	 */
	public boolean isQuerySuccess() {
		return querySuccess;
	}
	/**
	 * @param querySuccess the querySuccess to set
	 */
	public void setQuerySuccess(boolean querySuccess) {
		this.querySuccess = querySuccess;
	}

}
