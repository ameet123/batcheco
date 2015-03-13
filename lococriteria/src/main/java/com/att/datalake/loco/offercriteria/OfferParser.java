package com.att.datalake.loco.offercriteria;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.exception.OfferParserCode1100;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;

/**
 * parse the offer CSV file and generate a Model data structure with all offers
 * and relevant attributes in it this will be packed in {@link OfferSpecification} object
 * 
 * @author ac2211
 *
 */
@Component
public class OfferParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferParser.class);
	
	/**
	 * this is the offer file name
	 */
	private String filename;

	private CSVFormat offerFmt = CSVFormat.DEFAULT.withHeader("offerId", "criterionId", "criterionType",
			"criterionApplyObject", "criterionValues").withSkipHeaderRecord(true);

	/**
	 * parse the offer csv file and generate a List<Offer> object
	 * @return List<Offer> offers, one for each row in the offer file
	 */
	public List<OfferSpecification> parse() {
		List<OfferSpecification> offers = new ArrayList<OfferSpecification>();
		// so that we can do a contains ? query in O(1) as opposed to O(n) -> List
		Map<String, OfferSpecification> offerMap = new HashMap<String, OfferSpecification>();
		
		if (filename == null || filename.isEmpty()) {
			throw new LocoException(OfferParserCode1100.NO_OFFER_FILE_SET);
		}
		Iterable<CSVRecord> offerRecords;
		try {
			offerRecords = offerFmt.parse(new FileReader(filename));
		} catch (IOException e) {
			throw new LocoException(OfferParserCode1100.OFFER_FILE_READ_ERROR);
		}
		// iterate over the records and start packing attributes into the Offer
		// object
		char criterionType;
		List<Detail> details;
		Detail d;
		for (CSVRecord record : offerRecords) {

			OfferSpecification or = offerMap.get(record.get("offerId") );
			if (or == null) {
				LOGGER.debug("Adding new offer:{}", record.get("offerId"));
				or = new OfferSpecification();
				or.setOfferId(record.get("offerId"));
				offerMap.put(record.get("offerId"), or);
				offers.add(or);
			}

			// set details
			details = or.getDetails();
			d = new OfferSpecification.Detail();
			try {
				d.setCriterionId(Byte.parseByte(record.get("criterionId")));
			} catch (NumberFormatException e) {
				throw new LocoException(OfferParserCode1100.CRITERION_ID_NOT_A_NUMBER);
			}
			
			criterionType = record.get("criterionType").charAt(0);
			if (!Character.isLetter(criterionType)) {
				throw new LocoException(OfferParserCode1100.CRITERION_TYPE_NOT_VALID);
			}
			d.setCriterionType(criterionType);
			d.setCriterionApplyObject(getStringList(record.get("criterionApplyObject")));
			d.setCriterionValues(getStringList(record.get("criterionValues")));
			// add detail to list of details
			details.add(d);

		}
		return offers;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * first remove any carriage return/line feed, then split on "," with optional spaces
	 * @param s
	 * @return
	 */
	private List<String> getStringList(String s) {
		String[] sArray = s.replaceAll("\\r\\n|\\r|\\n", " ").split(",\\s*");

		if (sArray == null || sArray.length == 0) {
			throw new LocoException(OfferParserCode1100.ZERO_OR_INVALID_CRITERION_APPLY_OBJECTS_OR_VALUES);
		}
		return Arrays.asList(sArray);
	}
}