package com.att.datalake.loco.offerconfiguration.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.util.Strings;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferRepoCode1600;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;

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
	/**
	 * find by local directory. BAsed on the file picked up, we can get the dir
	 * and from there we would like to find the offer component.
	 * local dir for a component is unique in that table
	 * @param dir
	 * @return {@link OfferComponent}
	 */
	public OfferComponent findByDirectory(String dir) {
		if (Strings.isNullOrEmpty(dir)) {
			throw new LocoException(OfferRepoCode1600.LOCAL_DIR_NOT_VALID);
		}
		return compRepo.findByLocalDirectory(dir);
	}
	
	public Offer findByOfferId(String id) {
		if (Strings.isNullOrEmpty(id)) {
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
		if (Strings.isNullOrEmpty(offerId)) {
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
}