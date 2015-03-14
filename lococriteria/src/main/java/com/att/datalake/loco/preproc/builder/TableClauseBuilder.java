package com.att.datalake.loco.preproc.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;

/**
 * a class where based on list of columns and a table name, we can generate from
 * adn select clauses
 * 
 * @author ac2211
 *
 */
public class TableClauseBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableClauseBuilder.class);
	private String START_ALIAS = "`";

	@Autowired
	private SQLClauseBuilder sql;
	private String alias;

	private Map<String, String> selectMap;
	public TableClauseBuilder() {
		alias = START_ALIAS;
		selectMap = new HashMap<String, String>();
	}

	/**
	 * based on the detail step,generate sql clauses and store them
	 * 
	 * @param d
	 * @return
	 */
	public TableClauseBuilder addStep(PreProcSpec.ProcDetail d) {
		List<String> leftColumns = d.getLeftColumns();
		List<String> rightColumns = d.getRightColumns();
		String lAlias = getNextAlias();
		String rAlias = getNextAlias();
		selectMap.putAll(getSelectMap(leftColumns, lAlias, rightColumns, rAlias));
//		String select = sql.select(selectMap, null);
//		LOGGER.info("**** {}", select);
		return this;
	}

	public Map<String, String> getSelectMap() {
		return selectMap;
	}
	private String getNextAlias() {
		alias = String.valueOf((char) (alias.charAt(0) + 1));
		return alias;
	}

	/**
	 * given two sets of columns, generate a unique map with appropriate alias
	 * removing duplicates. We first get the left columns. Assumption is that
	 * left table is a superset or more closer to final look we also have to
	 * account for function such as MOD(BAN,1000)
	 * 
	 * @param lColumns
	 * @param lAlias
	 * @param rColumns
	 * @param rAlias
	 * @return
	 */
	private Map<String, String> getSelectMap(List<String> lColumns, String lAlias, List<String> rColumns, String rAlias) {
		Map<String, String> map = new HashMap<String, String>();
		for (String c : lColumns) {
			processColumnAndAdd(c, lAlias, map);
		}
		// now do right, the key being, if we find the column already in map,
		// skip
		for (String c : rColumns) {
			if (c.contains(" ")) {
				LOGGER.debug("checking alias of right col:{}", c.split("\\s+")[1]);
				if (map.containsKey(c.split("\\s+")[1])) {
					continue;
				}
			} else {
				if (map.containsKey(c)) {
					continue;
				}
			}
			processColumnAndAdd(c, rAlias, map);
		}

		return map;
	}

	/**
	 * sometimes the column may contain a function MOD(BAN,1000) in this case,
	 * we want to add the alias to the column right here and then tell the
	 * sqlbuilder to not add any alias by passing null
	 * 
	 * @param col
	 * @param alias
	 * @return
	 */
	private void processColumnAndAdd(String col, String alias, Map<String, String> map) {
		String processedCol;
		boolean nullAlias = false;
		if (col.contains("(")) {
			LOGGER.debug("Checking col:{} contains open paren", col);
			String regex = "(.*?)\\(([a-zA-Z]+)(.*)\\)(.*)";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(col);
			if (m.find()) {
				LOGGER.debug("Processing col:{}, match found for ) ", col);
				StringBuilder sb = new StringBuilder();
				sb.append(m.group(1)).append("(").append(alias).append(".").append(m.group(2)).append(m.group(3))
						.append(")").append(m.group(4));
				LOGGER.debug("function column processed:{}", sb.toString());
				processedCol = sb.toString();
				nullAlias = true;
			} else {
				processedCol = col;
			}
		} else {
			processedCol = col;
		}
		map.put(processedCol, (nullAlias)?null:alias);
	}

	private Map<String, String> getFromMap(String table, String alias) {
		return new HashMap<String, String>() {
			private static final long serialVersionUID = -5496651690513457483L;
			{
				put(table, alias);
			}
		};
	}

	/**
	 * is this an intermediate table with name starting with FILE
	 * 
	 * @param table
	 * @return
	 */
	private boolean isTransient(String table) {
		if (table.startsWith("FILE")) {
			return true;
		} else {
			return false;
		}
	}
}