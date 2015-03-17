package com.att.datalake.loco.preproc.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.util.Utility;

/**
 * Generate SQL from {@link PreProcSpec}
 * 
 * @author ac2211
 *
 */
@Component
public class CommonBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonBuilder.class);
	@Autowired
	TableClauseBuilder tb;
	
	private Map<String, String> offerSqlMap;
	/**
	 * iterate over the offers and for each, generate a sql and store it in a map
	 * @param specifications
	 */
	public void build(List<PreProcSpec> specifications) {
		offerSqlMap = new HashMap<String, String>();
		String sql;
		for (PreProcSpec p: specifications) {
			sql = tb.build(p.getProcDetail());
			LOGGER.debug("offer:{}\nsql:{}", p.getOfferId(), Utility.prettyPrint(sql));
			offerSqlMap.put(p.getOfferId(), sql);
		}
		LOGGER.debug("# of SQLs generated:{}", offerSqlMap.size());
	}
}