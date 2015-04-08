package com.att.datalake.loco.offercriteria.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.att.datalake.loco.offercriteria.OfferDetailToSqlBridge;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;
/**
 * the actual conversion of {@link OfferSpecification} and {@link Detail} to SQL is done by 
 * {@link AbstractOfferBuilder} after that assuming that the offer reads from a single table, 
 * the final SQL is straightforward splicing of individual elements
 * which is what this common class aims to do. The only variant is the FROM table name
 * @author ac2211
 *
 */
public abstract class CommonOffer extends AbstractOfferBuilder {

	@Autowired
	private OfferDetailToSqlBridge offerToSql;
	@Autowired
	private SQLStatementBuilder complete;

	private String from;
	
	protected void setFromTable(String name) {
		this.from = name;
	}
	@Override
	public String build(List<Detail> details) {
		Map<String, Object> resultMap = super.init(details);
		String groupBy = (String) resultMap.get("groupBy");
		String selectWithAgg = (String) resultMap.get("selectWithAgg");
		String where = (String) resultMap.get("where");

		return buildIndividualSql(selectWithAgg, where, from, groupBy);
	}

	private String buildIndividualSql(String select, String where, String from, String groupBy) {
		complete.reset();
		complete.doFrom(offerToSql.from(from, null));
		complete.doSelect(select);
		complete.doWhere(where);
		complete.doGroupBy(groupBy);
		String sql = complete.build();
		return sql;
	}
}