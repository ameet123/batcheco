package com.att.datalake.loco.sqlgenerator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.SqlBuilderCode1200;

/**
 * Use the {@link SQLClauseBuilder} to generate the entire SQL
 * to make prototype, we can add @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
 * @author ac2211
 *
 */
@Component
public class SQLStatementBuilder {

	private StringBuilder sb;
	private String select;
	private String from;
	private String where;
	private String group;
	
	public SQLStatementBuilder() {
		sb = new StringBuilder();
	}
	public void doNothing(){}
	
	public SQLStatementBuilder doSelect(String select) {
		this.select = select;
		return this;
	}
	public SQLStatementBuilder doFrom(String from) {
		this.from = from;
		return this;
	}
	public SQLStatementBuilder doWhere(String where) {
		this.where = where;
		return this;
	}
	
	public SQLStatementBuilder doGroupBy(String group) {
		this.group = group;
		return this;
	}
	public String build() {
		if (StringUtils.isEmpty(select)) {
			throw new LocoException(SqlBuilderCode1200.NO_SELECT_CLAUSE_SET);
		} else if (StringUtils.isEmpty(from)) {
			throw new LocoException(SqlBuilderCode1200.NO_FROM_CLAUSE_SET);
		}
		sb.append(select);
		sb.append(" ");
		sb.append(from);
		sb.append(" ");
		if (!StringUtils.isEmpty(where)) {
			sb.append(where);
			sb.append(" ");
		}
		if (isAggregate()) {
			sb.append(group);
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * need to do this so we can start fresh, all variables are emptied out
	 */
	public void reset() {
		sb.setLength(0);
		this.from = "";
		this.group = "";
		this.select = "";
		this.group = "";
		this.where = "";
	}
	
	/**
	 * does the select clause have any aggregate function requiring group by
	 * @return 
	 */
	private boolean isAggregate() {
		if (select.matches(".*(?:SUM|MAX|MIN|COUNT|AVG)\\s*\\(.*")) {
			// ensure that group by is done
			if (StringUtils.isEmpty(group)) {
				throw new LocoException(SqlBuilderCode1200.NO_GROUPBY_CLAUSE_SET);
			}
			return true;
		} else {
			return false;
		}
	}
}