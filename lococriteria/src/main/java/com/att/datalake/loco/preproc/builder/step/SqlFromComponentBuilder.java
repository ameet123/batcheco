package com.att.datalake.loco.preproc.builder.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offercriteria.model.PreProcOutputData;
import com.att.datalake.loco.offercriteria.model.PreProcOutputData.OutputDetailData;
import com.att.datalake.loco.offercriteria.model.PreProcProcessorData;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;
import com.att.datalake.loco.util.Utility;

/**
 * from individual components, build entire sql
 * 
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

	public String build(PreProcProcessorData processorDTO) {
		String finalSql = null;
		// to store generated sql to be used in union
		Map<String, String> tableSqlMap = new HashMap<String, String>();
		String select, from, where;
		PreProcOutputData output = processorDTO.getOutput();
		for (OutputDetailData o : output.getDetailData()) {
			select = sql.select(o.getSelectMap(), null);
			from = sql.from(o.getFromMap(), null);
			List<String> predicates = new ArrayList<String>();
			predicates.add(sql.joinPredicate(o.getPredicateMap()));
			where = sql.where(predicates, true);

			complete.reset();
			complete.doSelect(select);
			complete.doFrom(from);
			complete.doWhere(where);
			String sql = complete.build();
			tableSqlMap.put(o.getTableKey(), sql);
			LOGGER.trace("{} SQL:\n{}", o.getTableKey(), Utility.prettyPrint(sql));
		}

		// now generate comprehensive sql involving union if needed
		List<String> sqls = new ArrayList<String>();
		for (String t : output.getUnionItems()) {
			LOGGER.debug("Adding to Union:{}", t);
			String sql = tableSqlMap.get(t);
			if (StringUtils.isEmpty(sql)) {
				throw new LocoException(OfferParserCode1100.PREPROC_JOIN_TO_UNION_MISMATCH);
			}
			sqls.add(sql);
		}
		if (sqls.size() > 0) {
			LOGGER.debug("Generating sql from union of {} sqls", sqls.size());
			finalSql = sql.unionAll(sqls);
		} else {
			// assume that the join operation was self-sufficient
			// and only one entry was there
			LOGGER.debug("Generating sql from just join operations of size:{}", tableSqlMap.size());
			if (tableSqlMap.size() != 1) {
				throw new LocoException(OfferParserCode1100.PREPROC_OPERATIONS_NOT_COMPLETE);
			}
			finalSql = tableSqlMap.values().iterator().next();
		}
		return finalSql;
	}
}