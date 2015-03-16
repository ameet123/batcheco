package com.att.datalake.loco.preproc.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;
import com.att.datalake.loco.util.Utility;

/**
 * from individual components, build entire sql
 * @author ac2211
 *
 */
@Component
public class SqlFromComponentBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlFromComponentBuilder.class);
	@Autowired
	private SQLClauseBuilder sql;
	@Autowired
	private SQLStatementBuilder complete;
	/**
	 * we wire {@link TableClauseBuilder} since we need three different maps -
	 * select, from , where
	 * @param tb
	 */
	public String build(TableClauseBuilder tb) {
		String finalSql = null;
		// to store generated sql to be used in union
		Map<String, String> tableSqlMap = new HashMap<String, String>();
		String select, from, where;
		for (Entry<String, Map<String, String>> e: tb.getSelectMapByTable().entrySet()) {
			
			select = sql.select(e.getValue(), null);		
			from = sql.from(tb.getFromMapByTable().get(e.getKey()), null);
			List<String> predicates = new ArrayList<String>(); 
			predicates.add(sql.joinPredicate(tb.getPredicateMapByTable().get(e.getKey())));
			where = sql.where(predicates, true);
			
			complete.reset();
			complete.doSelect(select);
			complete.doFrom(from);
			complete.doWhere(where);
			String sql = complete.build();
			tableSqlMap.put(e.getKey(), sql);
			LOGGER.debug("{} SQL:\n{}", e.getKey(), Utility.prettyPrint(sql));
		}
		// now generate comprehensive sql involving union if needed
		List<String> sqls = new ArrayList<String>();
		for (String t: tb.getUnionList()) {
			LOGGER.debug("Adding to Union:{}", t);
			String sql = tableSqlMap.get(t);
			if (StringUtils.isEmpty(sql)) {
				throw new LocoException(OfferParserCode1100.PREPROC_JOIN_TO_UNION_MISMATCH);
			}
			sqls.add(sql);			
		}
		if (sqls.size()>0) {
			LOGGER.debug("Generating sql from union of {} sqls", sqls.size());
			finalSql = sql.unionAll(sqls);	
			LOGGER.debug("Final:\n{}", Utility.prettyPrint(finalSql));
		}
		return finalSql;
	}
}