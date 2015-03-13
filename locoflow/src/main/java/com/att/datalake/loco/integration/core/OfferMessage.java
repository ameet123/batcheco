package com.att.datalake.loco.integration.core;

import java.io.File;

import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;

/**
 * an abstraction wrapper for an offer file.
 * based on the data file for an offer, we will create a message obejct
 * this will contain the {@link File} object as well as the 
 * matching {@link Offer#getOfferId()} offerID. Additionally, we will pack 
 * the {@link OfferComponent} ID or object into it as well. This will enable fast
 * lookups against various aspects of an offer such as which target dir to 
 * move this file to in HDFS etc.
 * @author ac2211
 *
 */
public class OfferMessage {
	private File offerFile;
	private String offerId;
	private OfferComponent offerComponent;
	
	public OfferMessage(File f) {
		this.offerFile = f;
	}
	/**
	 * @return the offerFile
	 */
	public File getOfferFile() {
		return offerFile;
	}
	/**
	 * @param offerFile the offerFile to set
	 */
	public void setOfferFile(File offerFile) {
		this.offerFile = offerFile;
	}
	/**
	 * @return the offerId
	 */
	public String getOfferId() {
		return offerId;
	}
	/**
	 * @param offerId the offerId to set
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}
	/**
	 * @return the offerComponent
	 */
	public OfferComponent getOfferComponent() {
		return offerComponent;
	}
	/**
	 * @param offerComponent the offerComponent to set
	 */
	public void setOfferComponent(OfferComponent offerComponent) {
		this.offerComponent = offerComponent;
	}

}
