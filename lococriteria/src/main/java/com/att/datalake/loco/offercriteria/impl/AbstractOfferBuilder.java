package com.att.datalake.loco.offercriteria.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferSqlConversionCode1300;
import com.att.datalake.loco.offercriteria.OfferDetailToSqlBridge;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.util.OfferConstants;

/**
 * some common logic can be abstracted here , thus simplying the actual
 * implementation of {@link OfferBuilder}
 * @author ac2211
 *
 */
public abstract class AbstractOfferBuilder implements OfferBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractOfferBuilder.class);

	@Autowired
	private OfferDetailToSqlBridge offerToSql;
	@Autowired
	private SQLClauseBuilder sql;
	/**
	 * initialize and do the common processing. we return a Map of important variables such as
	 * groupBy, select, selectWithAgg, predicates.
	 * Rather than storing class level fields, we return a map so this method and the class can be 
	 * made thread-safe
	 * we iterate over details first to get all aggregates and build them out
	 * and then we do rounding so that can apply rounding to multiple aggregate elements
	 * if required
	 * @param d
	 * @return
	 */
	protected Map<String, Object> init(List<Detail> details) {
		List<String> predicates = new ArrayList<String>();
		String groupBy = null;
		String select = null;
		String selectWithAgg = null;
		LOGGER.debug("Total criteria size:{}", details.size());

		for (Detail d : details) {
			switch (d.getCriterionType()) {
			case 'E':
				LOGGER.debug("Exclusionary criterion...");
				predicates.add(offerToSql.exclusion(d));
				break;
			case 'A':
				LOGGER.debug("Aggregate criterion...");
				groupBy = offerToSql.groupBy(d);
				select = offerToSql.select(d, true);
				selectWithAgg = offerToSql.select(d, false);
				break;
			default:
				break;
			}
		}
		// assuming that rounding is for the aggregate transform and that it will not 
		// used on plain non-agg columns.
		for (Detail d : details) {
			switch (d.getCriterionType()) {
			case 'R':
				if (d.getCriterionApplyObject().size() != d.getCriterionValues().size()) {
					throw new LocoException(OfferSqlConversionCode1300.ROUNDING_COL_ALIAS_NUMBER_MISMATCH);
				}
				selectWithAgg = offerToSql.round(d, selectWithAgg);
				LOGGER.trace("After rounding in Offer Agg select:{}", selectWithAgg);
				break;
			default:
				break;
			}
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("where", sql.where(predicates, true));
		resultMap.put("groupBy", groupBy);
		resultMap.put("select", select);
		resultMap.put("selectWithAgg", selectWithAgg);
		return resultMap;
	}
	/**
	 * get the standard insert part
	 */
	protected String getInsertPrefix(String select) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OVERWRITE TABLE ");
		sb.append(OfferConstants.OFFER_DAILY_TABLE);
		sb.append(" ");
		sb.append(select);
		return sb.toString();
	}
}