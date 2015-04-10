package com.att.datalake.loco.batch.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offerconfiguration.model.OfferCriteria;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.att.datalake.loco.util.OfferConstants;

/**
 * This task follows the {@link CriteriaLoaderTasklet} based step.
 * Here we just want to verify the offer criteria from the database
 * as well as the local configuration object {@link LocoConfiguration}
 * 
 * @author ac2211
 *
 */
@Component
public class CriteriaVerifierTasklet extends AbstractLocoTasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(CriteriaVerifierTasklet.class);

	private final String STEP_NAME = "step-1:offer-criteria-verification";

	/**
	 * to verify data loaded in from previous steps
	 */
	@Autowired
	private LocoConfiguration config;
	@Autowired
	private OfferDAO dao;
	
	@Override
	public String getName() {
		return STEP_NAME;
	}

	/**
	 * the offer criteria SQL is just one for all offers
	 * since it contains a union. This is stored at {@link LocoConfiguration#getCriterionSqls()}
	 * and for the database, there is a separate table {@link OfferCriteria}
	 */
	@Override
	public void process(ChunkContext context) {
	
		String criteriaSql = config.getOfferCriteriaSql();
		String localExtractDir = config.getLocalExtractDir();
		if (StringUtils.isEmpty(localExtractDir) || StringUtils.isEmpty(criteriaSql)) {
			throw new LocoException(OfferParserCode1100.CRITERIA_SQL_OR_EXTRACT_DIR_NULL);
		}
		
		// validate db data
		OfferCriteria offerCriterion = dao.findByCriteriaId(OfferConstants.OFFER_CRITERIA_ID);
		if (StringUtils.isEmpty(offerCriterion.getOfferCriteriaSql()) || StringUtils.isEmpty(offerCriterion.getLocalExtractDir())) {
			throw new LocoException("From the database", OfferParserCode1100.CRITERIA_SQL_OR_EXTRACT_DIR_NULL);
		}
		LOGGER.debug("Local extract dir:{} and criterion SQL validated against config object and database", localExtractDir);
	}
}