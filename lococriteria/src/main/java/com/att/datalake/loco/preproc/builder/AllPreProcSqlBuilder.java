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

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferSqlConversionCode1300;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.loco.util.Utility;

/**
 * Generate SQL from {@link PreProcSpec}
 * 
 * @author ac2211
 *
 */
@Component
public class AllPreProcSqlBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(AllPreProcSqlBuilder.class);
	@Autowired
	StepsProcessingController tb;
	@Autowired
	private OfferDAO offerdao;

	/**
	 * iterate over the offers and for each, generate a sql and store it in a map
	 * @param specifications
	 * @return 
	 */
	public Map<String, String> build(List<PreProcSpec> specifications) {
		Map<String, String> offerSqlMap = new HashMap<String, String>();
		String sql;
		for (PreProcSpec p: specifications) {
			sql = tb.build(p.getProcDetail());
			LOGGER.debug("offer:{}\nsql:{}", p.getOfferId(), Utility.prettyPrint(sql));
			offerSqlMap.put(p.getOfferId(), sql);
		}
		LOGGER.debug("# of SQLs generated:{}", offerSqlMap.size());
		if (specifications.size() != offerSqlMap.size()) {
			throw new LocoException(OfferSqlConversionCode1300.PREPROC_SQL_TO_OFFERS_MISMATCH);
		}
		persist(offerSqlMap);
		return offerSqlMap;
	}
	
	/**
	 * persist the pre processing sql to the database for each offer
	 */
	private void persist(Map<String, String> sqlMap) {
		Offer o;
		List<Offer> offers = new ArrayList<Offer>();
		for (Entry<String, String> e: sqlMap.entrySet()) {
			o = offerdao.findByOfferId(e.getKey());
			LOGGER.debug("Found offer:{}", o.getOfferId());
			o.setOfferPreProcSql(e.getValue());
			offers.add(o);
		}
		List<Offer> saved = offerdao.saveOffer(offers);
		LOGGER.debug("{} offers saved to database", saved.size());
	}
}