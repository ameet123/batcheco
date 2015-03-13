package com.att.datalake.loco.offercriteria.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.OfferDetailToSqlBridge;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;
import com.att.datalake.loco.util.OfferConstants;
import com.att.datalake.loco.util.Utility;

/**
 * specific logic to build offer1
 * @author ac2211
 */
@Component
public class Offer1 extends AbstractOfferBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(Offer1.class);

	@Autowired
	private OfferDetailToSqlBridge offerToSql;
	@Autowired
	private SQLStatementBuilder complete;

	@Override
	public String build(List<Detail> details) {
		Map<String, Object> resultMap = super.init(details);
		String groupBy = (String) resultMap.get("groupBy");
		String select = (String) resultMap.get("select");
		String selectWithAgg = (String) resultMap.get("selectWithAgg");
		String where = (String) resultMap.get("where");

		String chargeSql = buildIndividualSql(select, where, OfferConstants.OFFER1_CHARGE_TABLE);
		String adjustmentSql = buildIndividualSql(select, where, OfferConstants.OFFER1_ADJUSTMENT_TABLE);

		// build complete
		return buildComplete(chargeSql, adjustmentSql, groupBy, selectWithAgg);
	}

	private String buildComplete(String s1, String s2, String groupBy, String select) {
		complete.reset();
		complete.doFrom(offerToSql.unionFrom(s1, s2));
		complete.doGroupBy(groupBy);
		complete.doSelect(select);
		String sql = complete.build();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(Utility.prettyPrint(sql));
		}
		// add the insert part
		sql = super.getInsertPrefix(sql);
		return sql;
	}

	private String buildIndividualSql(String select, String where, String from) {
		complete.reset();
		complete.doFrom(offerToSql.from(from, null));
		complete.doSelect(select);
		complete.doWhere(where);
		String sql = complete.build();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("\n\nfor Table:{} SQL:{}\n\n", from, Utility.prettyPrint(sql));
		}
		return sql;
	}
}