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
import com.att.datalake.loco.preproc.model.PreProcOutputData;
import com.att.datalake.loco.preproc.model.PreProcProcessorData;
import com.att.datalake.loco.preproc.model.PreProcOutputData.OutputDetailData;
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
	private SQLClauseBuilder sqlBuilder;
	@Autowired
	private SQLStatementBuilder complete;

	/**
	 * we are assuming that the sequence of operations is: 1. individual SQL
	 * involving joins across multiple tables 2. Union of SQLs generated from
	 * step 1 3. insertion into a table from step 1 or 2. ( 2 is optional)
	 * 
	 * @param processorDTO
	 * @return
	 */
	public String build(PreProcProcessorData processorDTO) {
		String finalSql = null;
		// to store generated sql to be used in union
		Map<String, String> tableSqlMap = new HashMap<String, String>();
		PreProcOutputData output = processorDTO.getOutput();
		// perform join
		buildJoin(output, tableSqlMap);
		// perform Union
		finalSql = buildUnion(output, tableSqlMap);
		// perform insert
		finalSql = buildInsert(processorDTO, finalSql);
		return finalSql;
	}

	private void buildJoin(PreProcOutputData output, Map<String, String> tableSqlMap) {
		String select, from, where;
		for (OutputDetailData o : output.getDetailData()) {
			select = sqlBuilder.select(o.getSelectMap(), null);
			from = sqlBuilder.from(o.getFromMap(), null);
			List<String> predicates = new ArrayList<String>();
			predicates.add(sqlBuilder.joinPredicate(o.getPredicateMap()));
			where = sqlBuilder.where(predicates, true);

			complete.reset();
			complete.doSelect(select);
			complete.doFrom(from);
			complete.doWhere(where);
			String sql = complete.build();
			tableSqlMap.put(o.getTableKey(), sql);
			LOGGER.trace("{} SQL:\n{}", o.getTableKey(), Utility.prettyPrint(sql));
		}
	}

	/**
	 * build insert into with validations
	 * 
	 * @param processorDTO
	 * @param sql
	 * @return
	 */
	private String buildInsert(PreProcProcessorData processorDTO, String sql) {
		Map<String, String> insertMap = processorDTO.getInsertMap();
		if (insertMap.size() == 0) {
			return sql;
		}
		// there should be only one entry here
		if (insertMap.size() > 1) {
			throw new LocoException(OfferParserCode1100.PREPROC_MULTIPLE_INSERT_INTO_INVALID);
		}
		String insertTable = insertMap.keySet().iterator().next();
		LOGGER.debug("Formulating final insert into clause for table:{}", insertTable);
		return sqlBuilder.insertInto(insertTable, sql, true);
	}

	private String buildUnion(PreProcOutputData output, Map<String, String> tableSqlMap) {
		String finalSql;
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
			finalSql = sqlBuilder.unionAll(sqls);
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