package com.att.datalake.loco.offercriteria.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferDataCode1700;
import com.att.datalake.loco.util.Utility;

/**
 * read offertable map properties file and extract
 * the map of offer id to individual offer hive table
 * @author ac2211
 *
 */
@Component
public class OfferTableMapProperties {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferTableMapProperties.class);
	
	private final static String OFFER_MAP_FILE = "offer_table_map.yml";
	private String offers;
	private Yaml yaml;
	
	public OfferTableMapProperties() {
		offers = Utility.classpathFileToString(OFFER_MAP_FILE, getClass());
		if (StringUtils.isEmpty(offers)) {
			throw new LocoException(OfferDataCode1700.OFFER_TABLE_MAP_FILE_ERROR);
		}
		yaml = new Yaml();
	}
	/**
	 * get map of offerId-> hive table
	 * @return
	 */
	public Map<String, String> getOfferTables() {
		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> offerTables = (Map<String, Map<String,String>>) yaml.load(offers);
		Map<String, String> offerMap = offerTables.get("offers");
		if (offerMap == null || offerMap.size() == 0) {
			throw new LocoException(OfferDataCode1700.OFFER_TABLE_INVALID_DATA);
		}
		LOGGER.debug("Size of offer-> table map:{}", offerMap.size());
		return offerMap;
	}
}