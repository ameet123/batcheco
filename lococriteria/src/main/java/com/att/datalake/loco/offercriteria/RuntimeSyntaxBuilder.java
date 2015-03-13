package com.att.datalake.loco.offercriteria;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.offercriteria.impl.OfferBuilder;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;

/**
 * given an {@link OfferSpecification} Json object, iterate over the offers and for each one,
 * build a runtime syntax, i.e. SQL in this case.
 * 
 * Important: At this time, the final list of columns is ASSUMED to be static and fixed
 * @author ac2211
 *
 */
@Component
public class RuntimeSyntaxBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeSyntaxBuilder.class);

	@Autowired
	@Qualifier("offer1")
	private OfferBuilder offer1;
	@Autowired
	@Qualifier("commonOffer1")
	private OfferBuilder commonOffer1;
	
	public List<String> build(List<OfferSpecification> offers) {
		String offerId;
		List<String>  sqls = new ArrayList<String>();
		for (OfferSpecification o: offers) {
			offerId = o.getOfferId();
			String offerClass = OfferIdToImplMap.OFFER_BUILDER_MAP.get(offerId);
			
			
			switch (offerClass) {
			case "Offer1":
				LOGGER.debug("Mapping impl class:{} for offer:{}", offerClass, offerId);
				sqls.add(offer1.build(o.getDetails()));
				break;
			case "CommonOffer1":
				LOGGER.debug("Mapping impl class:{} for offer:{}", offerClass, offerId);
				sqls.add(commonOffer1.build(o.getDetails()));
				break;
			default:
				break;
			}
		}
		return sqls;
	}
}