package com.att.datalake.loco.preproc.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;

/**
 * a class where based on list of columns and a table name, 
 * we can generate from adn select clauses
 * @author ac2211
 *
 */
public class TableClauseBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableClauseBuilder.class);
	@Autowired
	private SQLClauseBuilder sql;
	private String alias;
	private String select, from;
	/**
	 * based on the current alias we will get the next alias and get to work
	 * @param table
	 * @param columns
	 * @param currentAlias
	 */
	public TableClauseBuilder(String table, List<String> columns, String currentAlias) {
		alias = getNextAlias(currentAlias);
		select = sql.select(getSelectMap(columns, alias), null);
		from = sql.from(getFromMap(table, alias), null);
	}
	public String getFrom() {
		return from;
	}
	public String getSelect() {
		return select;
	}
	public String getAlias() {
		return alias;
	}
	private String getNextAlias(String s) {
		return String.valueOf((char)(s.charAt(0)+1));
	}
	private Map<String, String> getSelectMap(List<String> columns, String alias) {
		Map<String, String> map = new HashMap<String, String>();
		for (String c: columns) {
			map.put(c, alias);
		}
		return map;
	}
	private Map<String, String> getFromMap(String table, String alias) {
		return new HashMap<String, String>() {
			private static final long serialVersionUID = -5496651690513457483L;
			{
				put(table, alias);
			}
		};
	}
}