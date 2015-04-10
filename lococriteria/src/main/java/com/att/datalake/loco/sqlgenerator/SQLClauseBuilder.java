package com.att.datalake.loco.sqlgenerator;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * wholistic class to build various clauses of SQL. the individual clauses are
 * simple enough to be collected together. If not, then we will have many
 * singletons floating around. it can be changed as need be
 * 
 * @author ac2211
 *
 */
@Component
public class SQLClauseBuilder {
	private SimpleDateFormat defaultDateFmt = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat defaultTimestampFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static int AND_SUBTRACTOR = 5;
	private final static int UNION_SUBTRACTOR = 11;
	/**
	 * predicate is of the form - COL_A [NOT IN|IN] ( 'v1', 'v2'...) as well as
	 * join predicate a.id = b.id
	 * 
	 * @param predicates
	 * @param is
	 *            the list to be joined by AND or OR. true => AND
	 * @return
	 */
	public String where(List<String> predicates, boolean isAnded) {
		String joiner = (isAnded) ? " AND " : " OR ";
		if (predicates == null || predicates.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("WHERE ");
		for (String p : predicates) {
			sb.append(p);
			sb.append(joiner);
		}
		sb.setLength(sb.length() - AND_SUBTRACTOR);
		return sb.toString();
	}

	/**
	 * prepare a join clause
	 * 
	 * @param joinMap
	 * @return
	 */
	public String joinPredicate(Map<String, String> joinMap) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> e : joinMap.entrySet()) {
			sb.append(e.getKey());
			sb.append(" = ");
			sb.append(e.getValue());
			sb.append(" AND ");
		}
		sb.setLength(sb.length() - AND_SUBTRACTOR);
		return sb.toString();
	}

	/**
	 * this is just for inclusion/exclusion type of predicate in/not in
	 * appropriate alias needs to be passed along with col name
	 * 
	 * @param <T>
	 * @param column
	 *            to apply where clause predicate on
	 * @param values
	 *            applicable list of values in appropriate data type
	 * @param class type of values being sent - String, decimal etc...
	 * @param is
	 *            this NOT IN or IN
	 */
	public <T> String inOutPredicate(String column, List<T> values, Class<T> valueType, boolean isExclusion) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(" " + ((isExclusion) ? "NOT IN" : "IN") + " ( ");

		for (T o : values) {
			switch (valueType.getSimpleName()) {
			case "Integer":
			case "Short":
			case "Byte":
				sb.append(o + ",");
				break;
			case "Date":
				sb.append("'" + defaultDateFmt.format(o) + "',");
				break;
			case "Timestamp":
				sb.append("'" + defaultTimestampFmt.format(o) + "',");
				break;
			default:
				sb.append("'" + (String) o + "',");
				break;
			}
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * get a list of columns and create a SELECT clause out of, the map passed
	 * may have functions for one or more columns such as SUM, ROUND, COUNT etc.
	 * DO NOT overlap columns in List and Map for simplicity, pass the transform
	 * column with appropriate table alias if needed
	 * @param colMap map of columns and corresponding aliases if required
	 * @param colTransformMap map of columns and corresponding transform
	 * @return
	 */
	public String select(Map<String, String> colMap, Map<String, String> colTransformMap) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (Entry<String, String> e : colMap.entrySet()) {
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				sb.append(e.getValue());
				sb.append(".");
			}
			sb.append(e.getKey());
			sb.append(", ");
		}
		if (colTransformMap != null && colTransformMap.size() > 0) {
			for (Entry<String, String> e : colTransformMap.entrySet()) {
				sb.append(e.getValue().toUpperCase());
				sb.append("(");
				sb.append(e.getKey());
				sb.append("), ");
			}
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}

	/**
	 * convenience method to return SELECT * FROM <TABLE>;
	 * @param table
	 * @return
	 */
	public String selectAllFrom(String table) {
		Map<String, String> colMap = new HashMap<String, String>();
		colMap.put("*", null);
		String sel = select(colMap, null);
		colMap = new HashMap<String, String>();
		colMap.put(table, null);
		String from = from(colMap, null);
		StringBuilder sb = new StringBuilder();
		sb.append(sel);
		sb.append(" ");
		sb.append(from);
		return sb.toString();
	}
	/**
	 * given a map of columns and aliases , generate group by clause alias is
	 * optional
	 * 
	 * @param colMap
	 *            map of col=>alias
	 * @return
	 */
	public String groupBy(Map<String, String> colMap) {
		if (colMap == null || colMap.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("GROUP BY ");
		for (Entry<String, String> e : colMap.entrySet()) {
			if (e.getValue() != null && !e.getValue().isEmpty()) {
				sb.append(e.getValue());
				sb.append(".");
			}
			sb.append(e.getKey());
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}

	/**
	 * the map is tablename => alias. alias is optional in case of inline table,
	 * we need to wrap it in parenthesis
	 * 
	 * @param colMap
	 * @param inlineMap
	 *            is table inline true?
	 * @return
	 */
	public String from(Map<String, String> colMap, Map<String, Boolean> inlineMap) {
		if (colMap == null || colMap.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("FROM ");
		boolean inline;
		for (Entry<String, String> e : colMap.entrySet()) {
			inline = isInline(e.getKey(), inlineMap);
			if (inline) {
				sb.append("(");
			}
			sb.append(e.getKey());
			if (inline) {
				sb.append(")");
			}
			if (!StringUtils.isEmpty(e.getValue())) {
				sb.append(fromAlias(e.getValue()));
			}
			sb.append(", ");
		}
		sb.setLength(sb.length() - 2);
		return sb.toString();
	}

	/**
	 * based on a list of sql statements, generate a union all by joining
	 * 
	 * @param sqls
	 * @return
	 */
	public String unionAll(List<String> sqls) {
		if (sqls == null || sqls.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String s : sqls) {
			sb.append(s);
			sb.append(" UNION ALL ");
		}
		sb.setLength(sb.length() - UNION_SUBTRACTOR);
		return sb.toString();
	}
	/**
	 * insert  [overwrite] into table select...
	 * @param table
	 * @param query
	 * @param isOverwrite
	 * @return
	 */
	public String insertInto(String table, String query, boolean isOverwrite) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT ");
		if (isOverwrite) {
			sb.append("OVERWRITE ");			
		} else {
			sb.append("INTO ");
		}
		sb.append("TABLE ");
		sb.append(table);
		sb.append(" ");
		sb.append(query);
		return sb.toString();
	}

	/**
	 * check whether the passsed table has inline set to true
	 */
	private boolean isInline(String table, Map<String, Boolean> inlineMap) {
		if (table == null || table.isEmpty()) {
			return false;
		}
		if (inlineMap != null && !inlineMap.isEmpty()) {
			return inlineMap.containsKey(table);
		}
		return false;
	}

	/**
	 * check if the passed alias is not empty and then add it with a space at
	 * the front
	 * 
	 * @param s
	 * @return
	 */
	private String fromAlias(String s) {
		StringBuilder sb = new StringBuilder();
		if (s != null && !s.isEmpty()) {
			sb.append(" ");
			sb.append(s);
		}
		return sb.toString();
	}
}