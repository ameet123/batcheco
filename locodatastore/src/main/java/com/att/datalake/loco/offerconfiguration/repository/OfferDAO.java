package com.att.datalake.loco.offerconfiguration.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferRepoCode1600;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;
import com.att.datalake.loco.offerconfiguration.model.OfferCriteria;

/**
 * implementation class for all db interactions
 * @author ac2211
 *
 */
@Component
public class OfferDAO {

	@Autowired
	private OfferComponentRepository compRepo;
	@Autowired
	private OfferRepository offerRepo;
	@Autowired
	private OfferCriteriaRepository criteriaRepo;
	/**
	 * find by local directory. BAsed on the file picked up, we can get the dir
	 * and from there we would like to find the offer component.
	 * local dir for a component is unique in that table
	 * @param dir
	 * @return {@link OfferComponent}
	 */
	public OfferComponent findByDirectory(String dir) {
		if (StringUtils.isEmpty(dir)) {
			throw new LocoException(OfferRepoCode1600.LOCAL_DIR_NOT_VALID);
		}
		return compRepo.findByLocalDirectory(dir);
	}
	
	public Offer findByOfferId(String id) {
		if (StringUtils.isEmpty(id)) {
			throw new LocoException(OfferRepoCode1600.OFFER_ID_NOT_VALID);
		}
		return offerRepo.findOne(id);
	}
	public List<Offer> findAllOffers() {
		return offerRepo.findAll();
	}
	
	public Offer saveOffer(Offer o) {
		if (o == null) {
			throw new LocoException(OfferRepoCode1600.OFFER_TO_SAVE_IS_NULL);
		}
		return offerRepo.save(o);
	}
	public List<Offer> saveOffer(List<Offer> offers) {
		if (offers == null) {
			throw new LocoException(OfferRepoCode1600.OFFER_TO_SAVE_IS_NULL);
		}
		return offerRepo.save(offers);
	}
	public void deleteOffer(String offerId) {
		if (StringUtils.isEmpty(offerId)) {
			throw new LocoException(OfferRepoCode1600.OFFER_ID_NOT_VALID);
		}
		offerRepo.delete(offerId);
	}
	public long countOffers() {
		return offerRepo.count();
	}
	public long countComponents() {
		return compRepo.count();
	}
	public OfferCriteria findByCriteriaId(String id) {
		if (StringUtils.isEmpty(id)) {
			throw new LocoException(OfferRepoCode1600.OFFER_ID_NOT_VALID);
		}
		return criteriaRepo.findOne(id);		
	}
	public OfferCriteria saveCriteria(OfferCriteria criteria) {
		return criteriaRepo.save(criteria);
	}
	public int countCriteriaSql() {
		return (int) criteriaRepo.count();
	}
}