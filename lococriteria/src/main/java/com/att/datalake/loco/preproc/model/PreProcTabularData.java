package com.att.datalake.loco.preproc.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.att.datalake.loco.util.OfferParserUtil;

/**
 * at each step we capture the tables, step, columns data in a convenience model
 * class that way we don't have to deal with right/left columns/tables, aliases
 * we can encapsulate them here, thus making the {@link PreProcProcessorData}
 * class a little cleaner
 * 
 * @author ac2211
 *
 */
public class PreProcTabularData {
	/**
	 * starting alias which is one ASCII char less than lowercase 'a', this
	 * gives the first table an alias of a
	 */
	private final String START_ALIAS = "`";
	/**
	 * current alias by incrementing the ASCII value by 1
	 */
	private String alias;
	private List<String> leftColumns = null;
	private String leftAlias = null;
	private List<String> rightColumns;
	private String rightAlias;
	/**
	 * we need a map of alias by table because in forming a WHERE clause
	 * predicate, we need to know the matching table alias in case of a step
	 * involving left side transient table
	 * we avoid adding null aliases
	 */
	private Map<String, String> stepAliasMap;

	public PreProcTabularData() {
		alias = START_ALIAS;
		stepAliasMap = new HashMap<String, String>();
	}

	/**
	 * based on the step data, we set the left right columns and also adjust the
	 * aliases
	 * we keep this package private so that only {@link PreProcProcessorData} can call this
	 * method
	 * @param d
	 */
	void calibrate(PreProcSpec.ProcDetail d) {
		// process left if needed
		if (!OfferParserUtil.isTransient(d.getLeftTable())) {
			setLeftColumns(d.getLeftColumns());
			incrementLeftAlias();
		} else {
			setLeftColumns(null);
			nullifyLeftAlias();
		}
		if (!OfferParserUtil.isTransient(d.getRightTable())) {
			setRightColumns(d.getRightColumns());
			incrementRightAlias();
		} else {
			setRightColumns(null);
			nullifyRightAlias();
		}
		updateAliasMap(d);
	}

	/**
	 * update step alias with currently available data
	 */
	private void updateAliasMap(PreProcSpec.ProcDetail d) {
		if (!StringUtils.isEmpty(leftAlias)) {
			stepAliasMap.put(d.getLeftTable(), leftAlias);
		}
		stepAliasMap.put(d.getRightTable(), rightAlias);
	}

	// Utility methods
	public void incrementLeftAlias() {
		this.leftAlias = getNextAlias();
	}

	public void incrementRightAlias() {
		this.rightAlias = getNextAlias();
	}

	public void nullifyLeftAlias() {
		this.leftAlias = null;
	}

	public void nullifyRightAlias() {
		this.rightAlias = null;
	}

	// Getters/Setters
	public List<String> getLeftColumns() {
		return leftColumns;
	}

	public String getLeftAlias() {
		return leftAlias;
	}

	public List<String> getRightColumns() {
		return rightColumns;
	}

	public String getRightAlias() {
		return rightAlias;
	}

	public void setLeftColumns(List<String> leftColumns) {
		this.leftColumns = leftColumns;
	}

	public void setRightColumns(List<String> rightColumns) {
		this.rightColumns = rightColumns;
	}

	public Map<String, String> getStepAliasMap() {
		return stepAliasMap;
	}

	/**
	 * increment the current alias by adding 1 to the ASCII value
	 * 
	 * @return
	 */
	private String getNextAlias() {
		alias = String.valueOf((char) (alias.charAt(0) + 1));
		return alias;
	}
}