package com.att.datalake.loco.preproc.builder;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offercriteria.model.PreProcOperation;
import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.util.OfferParserUtil;

/**
 * build a predicate list to generate a where clause we need predicate list and
 * to build predicates we need join predicate map of columns and aliases
 * 
 * @author ac2211
 *
 */
@Component
public class PredicateBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(PredicateBuilder.class);

	/**
	 * we choose to keep the right side as the key, since the left side may be the same for 
	 * multiple records.
	 * @param d
	 * @param predicateMap
	 * @param rAlias
	 * @param lAlias
	 * @param aliasMap
	 */
	public void build(PreProcSpec.ProcDetail d, Map<String, String> predicateMap, String rAlias, String lAlias,
			Map<String, String> aliasMap) {
		LOGGER.debug("incoming for output:{} size:{}", d.getOutput(), predicateMap.size());
		// don't do anything if union operation
		if (d.getOp() == PreProcOperation.UNION.getValue()) {
			return;
		}
		String leftSide = getJoinColumn(d.getLeftColumns(), d.getLeftTable(), d.getMatchingTable(), d.getOpColumn(),
				aliasMap);
		String rightSide = getJoinColumn(d.getRightColumns(), d.getRightTable(), d.getMatchingTable(), d.getOpColumn(),
				aliasMap);
		
		predicateMap.put(rightSide, leftSide);
		LOGGER.debug("Output:{} left:{} right:{} outoigng size:{}", d.getOutput(), leftSide, rightSide, predicateMap.size());
	}

	/**
	 * sometimes the join column may be an alias name in the select list in such
	 * a case, we need to get the original name of the column if the table is
	 * transient, then just get matching table and return the join column
	 * 
	 * @return
	 */
	private String getJoinColumn(List<String> columns, String tableName, String matchingTable, String opColumn,
			Map<String, String> aliasMap) {
		String joinCol = null, alias = null;
		if (OfferParserUtil.isTransient(tableName)) {
			alias = aliasMap.get(matchingTable);
			joinCol = opColumn;
		} else {
			alias = aliasMap.get(tableName);
			for (String c : columns) {
				if (c.equals(opColumn)) {
					joinCol = c;
					break;
				}

				// for situation c = "SOR_INVARIANT_ID BAN" and opcol = BAN
				if (c.contains(opColumn)) {
					LOGGER.debug("Ok mostly alias, column:{} contains opCol:{}", c, opColumn);
					if (c.split("\\s+")[1].equals(opColumn)) {
						joinCol = c.split("\\s+")[0];
						break;
					} else {
						// assume that the op column exists in the table, but
						// select clause just either used an alias or had a
						// function around it
						joinCol = opColumn;
						break;
					}
				}
			}
		}
		if (joinCol == null) {
			throw new LocoException(OfferParserCode1100.PREPROC_OP_COL_NOT_FOUND_IN_SELECT_LIST);
		}
		return alias + "." + joinCol;
	}
}