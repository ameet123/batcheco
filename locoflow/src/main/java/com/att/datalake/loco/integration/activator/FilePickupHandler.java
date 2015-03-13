package com.att.datalake.loco.integration.activator;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferDataCode1700;
import com.att.datalake.loco.integration.core.OfferMessage;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;

/**
 * a service activator to process the picked up files from the inbound channel
 * adapter. It will eventually be responsible for ensuring that the file upload
 * has completed successfully and that we do not pickup an incomplete file.
 * Primarily this milestone will generate a message of parameter type
 * {@link OfferMessage} Using the directory of the file, we will also try to get
 * matching offer and offer component
 * 
 * @author ac2211
 *
 */
@Component
public class FilePickupHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(FilePickupHandler.class);

	@Autowired
	private OfferDAO offerdao;

	public Message<OfferMessage> handle(Message<File> msg) {
		File f = msg.getPayload();
		LOGGER.info("Received file:{}", f.getAbsolutePath());
		OfferMessage om = new OfferMessage(f);

		// get component
		OfferComponent oc = offerdao.findByDirectory(f.getParent());
		LOGGER.debug("*** dir:{}, comp name:{}", f.getParent());
		if (oc == null) {
			throw new LocoException(OfferDataCode1700.FILE_NOT_MATCH_COMPNENT).set("file", f.getAbsolutePath());
		}
		om.setOfferComponent(oc);
		try {
			om.setOfferId(oc.getOffer().getOfferId());
		} catch (Exception e) {
			throw new LocoException(OfferDataCode1700.NO_MATCHING_OFFER_FOR_COMPNENT);
		}
		return MessageBuilder.withPayload(om).copyHeaders(msg.getHeaders()).build();
	}
}