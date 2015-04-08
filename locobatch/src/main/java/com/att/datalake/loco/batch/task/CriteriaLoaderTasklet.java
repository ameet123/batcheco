package com.att.datalake.loco.batch.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferSqlConversionCode1300;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferCriteria;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.att.datalake.loco.offercriteria.OfferParser;
import com.att.datalake.loco.offercriteria.RuntimeSyntaxBuilder;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.util.OfferConstants;
import com.att.datalake.loco.util.Utility;
import com.google.gson.Gson;

/**
 * check for offer criteria file and if found, parse and load it
 * else load the List<{@link OfferSpecification}> from database
 * run the List of {@link OfferSpecification} through {@link RuntimeSyntaxBuilder}
 * to generate the list of SQL statements for each offer ID.
 * store these sqls in db as well as in the {@link LocoConfiguration} object
 * for downstream processing
 * 
 * @author ac2211
 *
 */
@Component
public class CriteriaLoaderTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaLoaderTasklet.class);

	private String filename;
	private final String STEP_NAME = "step-0:offer-criteria-loading";
	@Autowired
	private OfferParser op;
	@Autowired
	private OfferDAO dao;
	@Autowired
	private Gson gson;
	@Autowired
	private RuntimeSyntaxBuilder rb;
	
	@Value("${extract}")
	private String extractDir;
	/**
	 * to pack data in for downstream processing
	 */
	@Autowired
	private LocoConfiguration config;
	
	@Override
	public String getName() {
		return STEP_NAME;
	}

	public void setFilename(String file) {
		this.filename = file;
	}
	@Override
	public void process(ChunkContext context) {
		List<OfferSpecification> criteria;
		if (StringUtils.isEmpty(filename)) {
			LOGGER.debug("No offer criteria file passed, trying to load from database now.");
			criteria = fetchCriteria();
		} else {
			LOGGER.debug("Offer criteria file passed:{}", new File(filename));
			op.setFilename(filename);
			criteria = op.parse();
			LOGGER.info("Loaded {} offer criteria", criteria.size());
			// iterate over the specs and persist to db for future runs
			for (OfferSpecification os: criteria) {
				updateOffer(os);
			}
		}
		LOGGER.info("{} offer criteria ready for processing", criteria.size());
		String offerCriteriaSql = rb.build(criteria);
		if (StringUtils.isEmpty(offerCriteriaSql)) {
			throw new LocoException(OfferSqlConversionCode1300.OFFER_CRITERIA_SQL_GEN_ERROR);
		}
		// debug
		if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("=>"+Utility.prettyPrint(offerCriteriaSql));
		}
		saveSql(offerCriteriaSql);
	}
	/**
	 * fetch offer from db and create a new one if not found
	 * @param offerId
	 */
	private void updateOffer(OfferSpecification os) {
		Offer o = dao.findByOfferId(os.getOfferId());
		if (o == null) {
			o = new Offer();
			o.setOfferId(os.getOfferId());
		}
		o.setOfferCriteria(gson.toJson(os));
		dao.saveOffer(o);
	}
	/**
	 * fetch all criteria from db for generating sql
	 * @return 
	 */
	private List<OfferSpecification> fetchCriteria() {
		List<Offer> offers = dao.findAllOffers();
		List<OfferSpecification> criteria = new ArrayList<OfferSpecification>();
		if (offers == null || offers.size() == 0) {
			return criteria;
		}
		for (Offer o: offers) {
			criteria.add(gson.fromJson(o.getOfferCriteria(), OfferSpecification.class));
		}
		LOGGER.info("Fetched {} offer criteria from database", criteria.size());
		return criteria;
	}
	/**
	 * persist the newly generated sql statements for offer criteria to the database
	 * also save them to the {@link LocoConfiguration} store for
	 * downstream processing. This can be somewhat substantial data, so as the number
	 * of offers increases, this can be revisited
	 * @param offerSqlMap
	 */
	private void saveSql(String offerCriteriaSql) {
		// save it
		config.setOfferCriteriaSql(offerCriteriaSql);
		OfferCriteria crit = dao.findByCriteriaId(OfferConstants.OFFER_CRITERIA_ID);
		if (crit == null) {
			crit = new OfferCriteria();
			crit.setCriteriaId(OfferConstants.OFFER_CRITERIA_ID);
			if(StringUtils.isEmpty(extractDir)) {
				extractDir = OfferConstants.CRIT_LOCAL_EXTRACT_DIR;
			}
		}
		crit.setOfferCriteriaSql(offerCriteriaSql);
		dao.saveCriteria(crit);
	}
}