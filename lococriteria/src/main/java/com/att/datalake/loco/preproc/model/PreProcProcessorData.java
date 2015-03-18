package com.att.datalake.loco.preproc.model;

import java.util.ArrayList;
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

	private PreProcSpec.ProcDetail currentDetail;

	private PreProcTabularData tabularData;

	public PreProcProcessorData() {
		// initialize various data structures
		selectMapByTable = new LinkedHashMap<String, Map<String, String>>();
		fromMapByTable = new LinkedHashMap<String, Map<String, String>>();
		predicateMapByTable = new LinkedHashMap<String, Map<String, String>>();
		unionList = new ArrayList<String>();
		tabularData = new PreProcTabularData();
	}

	// Utility/Convenience methods

	/**
	 * for each step, when we calibrate, we also need to calibrate the columns
	 * and aliases we just delegate this to the {@link PreProcTabularData} class
	 * so we don't need to worry what and how it sets those variables
	 * essentially a new alias is generated if needed otherwise it's reset to
	 * null and column list is updated if not transient This can be used as a
	 * local calibration provider method, in which we can hide class specific
	 * processing that we don't want to expose to others
	 * 
	 * @param d
	 */
	public void calibrateLocal(PreProcSpec.ProcDetail d) {
		tabularData.calibrate(d);
	}

	/**
	 * to build from Map from current aliases and table names this is detail and
	 * specific and as cuh is encapsulated here rather than delegating to and
	 * external method; it's really internal detail how it's done
	 */
	public void buildFromMap() {
		if (tabularData.getLeftAlias() != null) {
			getCurrentFromMap().put(getCurrentDetail().getLeftTable(), tabularData.getLeftAlias());
		}
		getCurrentFromMap().put(getCurrentDetail().getRightTable(), tabularData.getRightAlias());
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
	 * prepare and return the output for consumption
	 * 
	 * @return
	 */
	public PreProcOutputData getOutput() {
		PreProcOutputData output = new PreProcOutputData();
		output.setUnionItems(unionList);

		for (Entry<String, Map<String, String>> e : selectMapByTable.entrySet()) {
			PreProcOutputData.OutputDetailData d = new PreProcOutputData.OutputDetailData();
			d.setTableKey(e.getKey());
			d.setSelectMap(e.getValue());
			d.setFromMap(fromMapByTable.get(e.getKey()));
			d.setPredicateMap(predicateMapByTable.get(e.getKey()));
			output.addOutputDetail(d);
		}
		return output;
	}

	// Getters/Setters

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

	public PreProcSpec.ProcDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(PreProcSpec.ProcDetail d) {
		this.currentDetail = d;
	}

	public PreProcTabularData getTabularData() {
		return tabularData;
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
}