package com.att.datalake.loco.offercriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferSqlConversionCode1300;
import com.att.datalake.loco.offercriteria.impl.OfferBuilder;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;

/**
 * based on offer detail build varioussql clauses
 * 
 * @author ac2211
 *
 */
@Component
public class OfferDetailToSqlBridge {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferDetailToSqlBridge.class);

	private Random rn;
	@Autowired
	private SQLClauseBuilder sql;

	public OfferDetailToSqlBridge() {
		rn = new Random();
	}

	/**
	 * based on {@link Detail} generate predicate
	 * 
	 * @param d
	 * @return
	 */
	public String exclusion(Detail d) {
		String predicate;
		// we are assuming that since it's a exclusionary predicate, it's on a
		// single column
		if (d.getCriterionApplyObject().size() != 1) {
			throw new LocoException(OfferSqlConversionCode1300.MULTIPLE_COLS_FOR_EXCLUSION);
		}
		// at the moment, we are assuming that the values are all String since
		// Offer class itself
		// only allows List<String> for values. but we can change that in future
		// in which case, we will have to figure out the class for the values
		predicate = sql.inOutPredicate(d.getCriterionApplyObject().get(0), d.getCriterionValues(), String.class, true);
		LOGGER.trace(" predicate:{}", predicate);
		return predicate;
	}

	public String groupBy(Detail d) {
		// build a map of columns and aliases, at the moment no aliases
		Map<String, String> colMap = new HashMap<>();
		for (String s : d.getCriterionApplyObject()) {
			colMap.put(s, null);
		}
		String groupBy = sql.groupBy(colMap);
		LOGGER.trace("group by:{}", groupBy);
		return groupBy;
	}

	/**
	 * for criterion type 'R' generate a new column we are assuming that this
	 * will go at the end so we can prefix comma trying to make it as generic as
	 * possible. this is only for Aggregate columns also rounding is applied on
	 * multiple columns SUM(COL_1) => ROUND(SUM(COL_1)) as ALIAS1
	 * 
	 * @param d
	 * @param select
	 *            stmt generated so far
	 * @return
	 */
	public String round(Detail d, String select) {
		String finalSelect = select;
		String[] trans;
		String col;
		String roundCol = null;

		for (int i = 0; i < d.getCriterionValues().size(); i++) {
			StringBuilder sb = new StringBuilder();
			trans = extractTransform(d.getCriterionValues().get(i));
			col = trans[1];
			sb.append(trans[0]);
			sb.append("(");
			// need to check whether this is on aggregate or not
			if (finalSelect.matches(".*\\(" + col + "\\).*")) {
				Pattern p = Pattern.compile(".*,\\s?([a-zA-Z]+\\(" + col + "\\)).*");
				Matcher m = p.matcher(finalSelect);
				if (m.find()) {
					sb.append(m.group(1));
					roundCol = m.group(1);
				} else {
					throw new LocoException(OfferSqlConversionCode1300.ROUND_TRANSFORM_ON_AGGREGATE_COL_ERROR);
				}
			} else {
				LOGGER.debug("no aggregate transform matched, error");
				throw new LocoException(OfferSqlConversionCode1300.ROUND_TRANSFORM_ON_AGGREGATE_COL_ERROR);
			}
			sb.append(") as ");
			sb.append(d.getCriterionApplyObject().get(i));
			finalSelect = finalSelect.replace(roundCol, sb.toString());
			finalSelect = finalSelect + ", ";
		}
		return finalSelect.substring(0, finalSelect.length() - 2);
	}

	/**
	 * if aggInSelect is true, that means we don't really want the aggregate
	 * clause, rather we just want to get the column name into select we use
	 * linkedhashmpa, since the order of keys is important.
	 * 
	 * @param d
	 * @param aggInSelect
	 * @return
	 */
	public String select(Detail d, boolean aggInSelect) {
		Map<String, String> colMap = new LinkedHashMap<>();
		for (String s : d.getCriterionApplyObject()) {
			colMap.put(s, null);
		}
		Map<String, String> colTransMap = new HashMap<>();
		String[] trans;
		for (String s : d.getCriterionValues()) {
			trans = extractTransform(s);
			if (aggInSelect) {
				colMap.put(trans[1], null);
				continue;
			}
			colTransMap.put(trans[1], trans[0]);
		}
		String select = sql.select(colMap, colTransMap);
		LOGGER.trace("select:{}", select);
		return select;
	}

	public String unionFrom(String select1, String select2) {
		// random 4 digit number
		int FOUR_DIGIT_MIN = 1000;
		int FOUR_DIGIT_MAX = 9000;
		int r = rn.nextInt(FOUR_DIGIT_MAX) + FOUR_DIGIT_MIN; 
		List<String> stmts = new ArrayList<String>();
		stmts.add(select1);
		stmts.add(select2);
		String unionTable = sql.unionAll(stmts);
		Map<String, String> from2TableMap = new HashMap<String, String>();
		from2TableMap.put(unionTable, "vw_" + r);
		Map<String, Boolean> inlineMap = new HashMap<String, Boolean>();
		inlineMap.put(unionTable, true);
		String from = sql.from(from2TableMap, inlineMap);
		LOGGER.trace("Union from:{}\n", from);
		return from;
	}

	/**
	 * having from here alleviates the need to have {@link SQLClauseBuilder} in
	 * actual {@link OfferBuilder} implementations
	 * 
	 * @param table
	 * @param alias
	 * @return
	 */
	public String from(String table, String alias) {
		return sql.from(new HashMap<String, String>() {
			private static final long serialVersionUID = -7379362804661733067L;
			{
				put(table, alias);
			}
		}, null);
	}

	/**
	 * out of a string such as SUM(colA) extract the transform and column
	 * 
	 * @param s
	 * @return
	 */
	private String[] extractTransform(String s) {
		Pattern p = Pattern.compile("([a-zA-Z]+)\\((\\w+)\\)");
		Matcher m = p.matcher(s);
		String[] colTrans = new String[2];
		if (m.find()) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("1:" + m.group(1) + " 2:" + m.group(2));
			}
			colTrans[0] = m.group(1);
			colTrans[1] = m.group(2);
		} else {
			throw new LocoException(OfferSqlConversionCode1300.AGGREGATE_FUN_SPEC_ERROR);
		}
		return colTrans;
	}
}