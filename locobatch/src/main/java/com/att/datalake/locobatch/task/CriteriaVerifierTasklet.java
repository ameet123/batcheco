package com.att.datalake.locobatch.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.att.datalake.locobatch.shared.LocoConfiguration;
import com.att.datalake.locobatch.shared.LocoConfiguration.RuntimeData;

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

	@Override
	public void process() {
		RuntimeData data;
		String criteriaSql;
		// first check local data
		for (String offerId: config.offerIterator()) {
			data = config.get(offerId);
			criteriaSql = data.getCriteriaSql();
			Assert.state(!StringUtils.isEmpty(criteriaSql));
			LOGGER.info("Validated offer:{} to have criteria sql", offerId);
		}
		// validate db data
		List<Offer> offers = dao.findAllOffers();
		
		for (Offer o: offers) {
			criteriaSql = o.getOfferCriteriaSql();
			Assert.state(!StringUtils.isEmpty(criteriaSql));
			LOGGER.info("Validated offer:{} to have criteria sql stored in the database", o.getOfferId());
		}
	}
}