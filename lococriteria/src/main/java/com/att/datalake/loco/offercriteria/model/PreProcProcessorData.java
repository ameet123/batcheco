package com.att.datalake.loco.offercriteria.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.datalake.loco.util.Utility;

/**
 * a model/datastructure class to simplify the preprocessing specifications. The
 * preprocessing controller makes use of various maps and they are difficult to
 * deal with requiring deeper udnerstanding of the fields. This class will
 * encapsulate such understanding and make the interactions simpler
 * 
 * @author ac2211
 *
 */
public class PreProcProcessorData {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcProcessorData.class);
	/**
	 * starting alias which is one ASCII char less than lowercase 'a', this
	 * gives the first table an alias of a
	 */
	private final String START_ALIAS = "`";

	/**
	 * current alias by incrementing the ASCII value by 1
	 */
	private String alias;
	/**
	 * for each relevant output table, store the map of select columns and
	 * respective alias
	 */
	private Map<String, Map<String, String>> selectMapByTable;
	/**
	 * from table map is also generated as select above
	 */
	private Map<String, Map<String, String>> fromMapByTable;
	/**
	 * map of join predicates by aliases to be used for generating the where
	 * clause key => output table, val=> Map of predicates made up of
	 * left=>right side includes alias
	 */
	private Map<String, Map<String, String>> predicateMapByTable;
	/**
	 * we need a map of alias by table because in forming a WHERE clause
	 * predicate, we need to know the matching table alias in case of a step
	 * involving left side transient table
	 */
	private Map<String, String> stepAliasMap;
	/**
	 * list of tables/files which need union operation
	 */
	private List<String> unionList;
	/**
	 * value of the output from the previous step;
	 */
	private String prevOutput;
	/**
	 * value of the operation from previous step
	 */
	private char prevOp;

	private int prevStep;

	private List<String> leftColumns = null;
	private String leftAlias = null;
	private List<String> rightColumns;
	private String rightAlias;

	private PreProcSpec.ProcDetail currentDetail;

	public PreProcProcessorData() {
		alias = START_ALIAS;
		// initialize various data structures
		selectMapByTable = new LinkedHashMap<String, Map<String, String>>();
		fromMapByTable = new LinkedHashMap<String, Map<String, String>>();
		predicateMapByTable = new LinkedHashMap<String, Map<String, String>>();
		stepAliasMap = new HashMap<String, String>();
		unionList = new ArrayList<String>();
	}

	/**
	 * print the maps for all the sql clauses
	 */
	public void debugPrint() {
		String underline = Utility.pad("-", 80, '-');
		LOGGER.debug("Size of Select Map:{}", selectMapByTable.size());
		for (Entry<String, Map<String, String>> e : selectMapByTable.entrySet()) {
			MapUtils.debugPrint(System.out, e.getKey(), e.getValue());
		}
		System.out.println(underline);
		LOGGER.debug("Size of from Map:{}", fromMapByTable.size());
		for (Entry<String, Map<String, String>> e : fromMapByTable.entrySet()) {
			MapUtils.debugPrint(System.out, e.getKey(), e.getValue());
		}
		System.out.println(underline);
		LOGGER.debug("Size of from Map:{}", predicateMapByTable.size());
		for (Entry<String, Map<String, String>> e : predicateMapByTable.entrySet()) {
			MapUtils.debugPrint(System.out, e.getKey(), e.getValue());
		}
	}

	public void addNewMapEntries(String output) {
		selectMapByTable.put(output, new LinkedHashMap<String, String>());
		fromMapByTable.put(output, new LinkedHashMap<String, String>());
		predicateMapByTable.put(output, new LinkedHashMap<String, String>());
	}

	public void updateMapEntries(String output) {
		LOGGER.debug("Removing select,from maps key:{} and adding that entry to new key:{}", prevOutput, output);
		selectMapByTable.put(output, selectMapByTable.get(prevOutput));
		selectMapByTable.remove(prevOutput);
		fromMapByTable.put(output, fromMapByTable.get(prevOutput));
		fromMapByTable.remove(prevOutput);
		predicateMapByTable.put(output, predicateMapByTable.get(prevOutput));
		predicateMapByTable.remove(prevOutput);
	}

	public void setStepCompletion() {
		prevOutput = currentDetail.getOutput();
		prevOp = currentDetail.getOp();
		prevStep = currentDetail.getStep();
	}

	public void processUnion() {
		unionList.add(currentDetail.getLeftTable());
		unionList.add(currentDetail.getRightTable());
	}

	/**
	 * get the map matching the current step output table
	 * 
	 * @return
	 */
	public Map<String, String> getCurrentSelectMap() {
		return selectMapByTable.get(currentDetail.getOutput());
	}

	public Map<String, String> getCurrentFromMap() {
		return fromMapByTable.get(currentDetail.getOutput());
	}

	public Map<String, String> getCurrentPredicateMap() {
		return predicateMapByTable.get(currentDetail.getOutput());
	}

	public Map<String, String> getStepAliasMap() {
		return stepAliasMap;
	}

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

	public PreProcSpec.ProcDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(PreProcSpec.ProcDetail d) {
		this.currentDetail = d;
	}

	public List<PreProcOutputData> getOutput() {
		List<PreProcOutputData> outputs = new ArrayList<PreProcOutputData>();

		for (Entry<String, Map<String, String>> e : selectMapByTable.entrySet()) {
			PreProcOutputData output = new PreProcOutputData();
			output.setTableKey(e.getKey());
			output.setSelectMap(e.getValue());
			output.setFromMap(fromMapByTable.get(e.getKey()));
			output.setPredicateMap(predicateMapByTable.get(e.getKey()));
			outputs.add(output);
		}
		return outputs;
	}

	/**
	 * update step alias with currently available data
	 */
	public void updateAliasMap() {
		stepAliasMap.put(currentDetail.getLeftTable(), leftAlias);
		stepAliasMap.put(currentDetail.getRightTable(), rightAlias);
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

	// For NOW, later on we wneed to refactor
	public Map<String, Map<String, String>> getSelectMapByTable() {
		return selectMapByTable;
	}

	public Map<String, Map<String, String>> getFromMapByTable() {
		return fromMapByTable;
	}

	public Map<String, Map<String, String>> getPredicateMapByTable() {
		return predicateMapByTable;
	}

	public List<String> getUnionList() {
		return unionList;
	}

	public String getPrevOutput() {
		return prevOutput;
	}

	public int getPrevStep() {
		return prevStep;
	}

	public char getPrevOp() {
		return prevOp;
	}

	public void setPrevOp(char prevOp) {
		this.prevOp = prevOp;
	}
	
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

	public void setLeftColumns(List<String> leftColumns) {
		this.leftColumns = leftColumns;
	}

	public void setRightColumns(List<String> rightColumns) {
		this.rightColumns = rightColumns;
	}
}