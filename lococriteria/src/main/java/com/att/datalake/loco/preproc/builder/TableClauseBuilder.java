package com.att.datalake.loco.preproc.builder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offercriteria.model.PreProcOperation;
import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;

/**
 * a class where based on list of columns and a table name, we can generate from
 * and select clauses we accept the details object and iterate over it applying
 * all the logic of how to construct the final SQL. This would involve checking
 * for "join" and "union" operations matching the output of one step to the
 * input of the next
 * 
 * We need to use scope prototype, because we need new instantion of class the
 * reason being there are many maps - select, from, where which may be hard to
 * manage if not at class level, at least at the moment.
 * 
 * @author ac2211
 *
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TableClauseBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableClauseBuilder.class);
	private String START_ALIAS = "`";

	@Autowired
	private SelectColMapBuilder selectBuilder;
	@Autowired
	private SQLClauseBuilder sql;
	private String alias;

	private final String TRANSIENT_FILE_REGEX = "^FILE.*";
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
	 * using {@link LinkedHashMap} to preserve the order of columns
	 * initialize the select, from maps.
	 */
	public TableClauseBuilder() {
		System.out.println("------ In TableClauseBuilder constructor NEW ---------");
		alias = START_ALIAS;
		selectMapByTable = new LinkedHashMap<String, Map<String, String>>();
		fromMapByTable = new LinkedHashMap<String, Map<String, String>>();
	}

	/**
	 * we iterate over all the records and group them in the order they appear
	 * for specific processing. we collect all join operations and we collect
	 * all the union operations
	 * 
	 */
	public void build(List<PreProcSpec.ProcDetail> details) {
		String prevOutput = null, output, left;
		char prevOp = 0;
		int step = 0;
		// iterate over the list of steps
		for (PreProcSpec.ProcDetail d : details) {
			left = d.getLeftTable();
			output = d.getOutput();
			// validation of data
			validateChain(prevOutput, left, step, d.getStep());

			if (d.getOp() == PreProcOperation.JOIN.getValue()) {
				// Process JOIN
				processStep(d, prevOutput, prevOp);
			} else if (d.getOp() == PreProcOperation.UNION.getValue()) {
				// Process Union
			}
			// set current to previous
			prevOutput = output;
			step = d.getStep();
			prevOp = d.getOp();
		}

		// for DEBUG
		LOGGER.debug("Size of Select Map:{}", selectMapByTable.size());
		for (Entry<String, Map<String, String>> e : selectMapByTable.entrySet()) {
			MapUtils.debugPrint(System.out, e.getKey(), e.getValue());
		}
		System.out.println("----------------------------------------");
		LOGGER.debug("Size of from Map:{}", fromMapByTable.size());
		for (Entry<String, Map<String, String>> e : fromMapByTable.entrySet()) {
			MapUtils.debugPrint(System.out, e.getKey(), e.getValue());
		}
	}

	/**
	 * based on the detail step,generate sql clauses and store them design: we
	 * do the alias and map fetch here rather than in buildSelectMap() because,
	 * we will need the alias for "from" map as well. So we need to do it in a
	 * common place outside "select" methods.
	 * 
	 * Note: We only pass the right for transient left. This assumes that the
	 * left will NOT have a modification of columns over the previous
	 * incarnation, i.e. prev out FILE1 and curr left FILE1 have the same SELECT
	 * columns Left is passed only in case of non-transient tables
	 * 
	 * @param d
	 * @return
	 */
	public TableClauseBuilder processStep(PreProcSpec.ProcDetail d, String prevOutput, char prevOp) {
		// prepare the maps for this step
		setStepRelatedMaps(d, prevOutput, prevOp);
		
		// Pre requisites
		List<String> leftColumns = null;
		String lAlias = null;
		// process left if needed
		if (!isTransient(d.getLeftTable())) {
			leftColumns = d.getLeftColumns();
			lAlias = getNextAlias();
		}
		List<String> rightColumns = d.getRightColumns();
		String rAlias = getNextAlias();
		LOGGER.debug("LEFT:{} ALIAS:{} right:{}, RALIAS:{}", d.getLeftTable(), lAlias, d.getRightTable(), rAlias);

		// Process Select
		Map<String, String> selectMap = selectMapByTable.get(d.getOutput());
		selectMap = selectBuilder.build(selectMap, rightColumns, rAlias, leftColumns, lAlias);
		if (LOGGER.isTraceEnabled()) {
			MapUtils.debugPrint(System.out, d.getOutput(), selectMap);
		}
		
		// Process From
		Map<String, String> fromMap = fromMapByTable.get(d.getOutput());
		fromMap = buildFromMap(d, fromMap, rAlias, lAlias);

//		 String select = sql.select(selectMap, null);
		// LOGGER.info("**** {}", select);
		return this;
	}

	/**
	 * if a new group is started, then get a new map, otherwise get the old one
	 * The old one matches the previous output table/file
	 * Do this for all relavant clauses - select, from ...
	 * @param d
	 * @return
	 */
	private void setStepRelatedMaps(PreProcSpec.ProcDetail d, String prevOutput, char prevOp) {
		Map<String, String> selectMap;
		if (isNewGroup(prevOutput, d.getLeftTable(), prevOp, d.getOp())) {
			// add this to the selectMapByTable
			selectMapByTable.put(d.getOutput(), new LinkedHashMap<String, String>());
			fromMapByTable.put(d.getOutput(), new LinkedHashMap<String, String>());
		} else {
			LOGGER.debug("Getting prev output:{} map", prevOutput);
			selectMap = selectMapByTable.get(prevOutput);
			// replace the map matching prevOutput with a new entry for this
			// output
			selectMapByTable.put(d.getOutput(), selectMap);
			selectMapByTable.remove(prevOutput);
			
			
			Map<String, String> fromMap = fromMapByTable.get(prevOutput);
			fromMapByTable.put(d.getOutput(), fromMap);
			fromMapByTable.remove(prevOutput);
			
			LOGGER.debug("Removing select,from maps key:{} and adding that entry to new key:{}", prevOutput, d.getOutput());
			
		}
	}

	/**
	 * build from clause for the step. this is small enough so it can be added here rather than a dedicated class
	 * @param d
	 * @param fromMap
	 * @param rAlias
	 * @param lAlias
	 * @return
	 */
	public Map<String, String> buildFromMap(PreProcSpec.ProcDetail d, Map<String, String> fromMap, String rAlias, String lAlias) {
		if (lAlias != null) {
			fromMap.put(d.getLeftTable(), lAlias);
		}
		fromMap.put(d.getRightTable(), rAlias);
		return fromMap;
	}
	/**
	 * if the current left table is different from the prev output OR prevOP is
	 * diff from currentOp(from join to union) then we need to start a new
	 * "group" for the operations as such, we need to generate new storage
	 * structures - maps for select etc.
	 * 
	 * @param prevOutput
	 * @param currentLeft
	 * @param prevOp
	 * @param currentOp
	 * @return
	 */
	private boolean isNewGroup(String prevOutput, String currentLeft, char prevOp, char currentOp) {
		if (prevOutput == null || !prevOutput.equals(currentLeft) || prevOp != currentOp) {
			return true;
		}
		return false;
	}

	/**
	 * if the current file is first entry or is not a transient table, i.e.
	 * starting with FILE then just return, all izz well
	 * 
	 * @param p
	 * @param c
	 */
	private void validateChain(String p, String c, int prevStep, int currStep) {
		if ((prevStep + 1) != currStep) {
			throw new LocoException(OfferParserCode1100.PREPROC_STEPS_NOT_IN_ORDER);
		}
		if (StringUtils.isEmpty(p) || !isTransient(c)) {
			return;
		}
		LOGGER.debug("Checking output:{} current left:{}", p, c);
		if (!p.equals(c)) {
			throw new LocoException(OfferParserCode1100.PREPROC_IN_OUT_NOT_SEQUENTIAL);
		}
	}

	private boolean isTransient(String table) {
		return table.matches(TRANSIENT_FILE_REGEX);
	}

	private String getNextAlias() {
		alias = String.valueOf((char) (alias.charAt(0) + 1));
		return alias;
	}
}