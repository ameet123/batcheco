package com.att.datalake.loco.preproc;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import com.att.datalake.loco.preproc.model.PreProcOperation;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.loco.preproc.model.PreProcSpec.ProcDetail;
import com.att.datalake.loco.util.Utility;

/**
 * parse CSV into {@link PreProcSpec} json object
 * 
 * @author ac2211
 *
 */
@Component
public class PreProcessingParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreProcessingParser.class);

	private final String offerId = "offerId";
	private String preProcFile;

	private CSVFormat preProcFmt = CSVFormat.DEFAULT.withHeader(offerId, "step", "leftTable", "leftColumns",
			"rightTable", "rightColumns", "op", "opColumn", "matchingTable", "output").withSkipHeaderRecord(true);

	/**
	 * iterate over the csv file and create a List<PreProcSpec> while performing
	 * validations as needed
	 * 
	 * @return
	 */
	public List<PreProcSpec> parse() {
		List<PreProcSpec> preSpecList = new ArrayList<PreProcSpec>();
		// so that we can do a contains ? query in O(1) as opposed to O(n) ->List
		Map<String, PreProcSpec> preProcMap = new HashMap<String, PreProcSpec>();

		Iterable<CSVRecord> preProcRecords = recordIterator();
		// iterate over the records and start packing attributes into the Offer object
		List<ProcDetail> details;
		ProcDetail d;
		for (CSVRecord record : preProcRecords) {
			// perform validations on this row
			doValidations(record);

			PreProcSpec pr = buildNewSpecRecord(record.get(offerId), preProcMap, preSpecList);
			// set details
			details = pr.getProcDetail();
			d = new PreProcSpec.ProcDetail();
			d.setStep(Integer.parseInt(record.get("step")));
			d.setLeftTable(record.get("leftTable"));
			d.setRightTable(record.get("rightTable"));
			d.setOutput(record.get("output"));
			d.setOp(record.get("op").charAt(0));
			d.setOpColumn(record.get("opColumn"));
			d.setMatchingTable(record.get("matchingTable"));

			// LISTS
			d.setLeftColumns(Utility.getStringList(record.get("leftColumns")));
			d.setRightColumns(Utility.getStringList(record.get("rightColumns")));

			// add detail to list of details
			details.add(d);
		}
		return preSpecList;
	}

	public String getPreProcFile() {
		return preProcFile;
	}

	/**
	 * To be called before calling the parse method
	 * 
	 * @param preProcFile
	 *            the preProcFile to set
	 */
	public void setPreProcFile(String preProcFile) {
		this.preProcFile = preProcFile;
	}

	/**
	 * file validation and return an iterable of records
	 * 
	 * @return
	 */
	private Iterable<CSVRecord> recordIterator() {
		
		Iterable<CSVRecord> preProcRecords;
		try {
			preProcRecords = preProcFmt.parse(new FileReader(preProcFile));
		} catch (IOException e) {
			throw new LocoException(e, OfferParserCode1100.PREPROC_FILE_READ_ERROR);
		}
		return preProcRecords;
	}

	/**
	 * check whether we have encountered this offer before, if not, create a new
	 * one otherwise get the existing one and return
	 * 
	 * @param offerId
	 * @param preProcMap
	 * @param preSpecList
	 * @return PreProcSpec
	 */
	private PreProcSpec buildNewSpecRecord(String offerId, Map<String, PreProcSpec> preProcMap,
			List<PreProcSpec> preSpecList) {
		PreProcSpec pr = preProcMap.get(offerId);
		if (pr == null) {
			LOGGER.debug("Adding new offer:{}", offerId);
			pr = new PreProcSpec();
			pr.setOfferId(offerId);
			preProcMap.put(offerId, pr);
			preSpecList.add(pr);
		}
		return pr;
	}

	/**
	 * all csv record validations
	 * 
	 * @param record
	 */
	private void doValidations(CSVRecord record) {
		List<String> list = Utility.getStringList(record.get("rightColumns"));
		if (list == null || list.size() == 0) {
			throw new LocoException(OfferParserCode1100.RIGHT_TABLE_COLUMNS_NOTEXIST);
		}
		list = Utility.getStringList(record.get("leftColumns"));
		if (list == null || list.size() == 0) {
			throw new LocoException(OfferParserCode1100.LEFT_TABLE_COLUMNS_NOTEXIST);
		}
		char c = record.get("op").charAt(0);
		if (!Character.isLetter(c)
				|| ((c != PreProcOperation.JOIN.getValue()) 
						&& (c != PreProcOperation.UNION.getValue()) 
						&& (c != PreProcOperation.INSERT.getValue()))) {
			throw new LocoException(OfferParserCode1100.PREPROC_MERGE_OP_NOT_VALID);
		}
		try {
			Integer.parseInt(record.get("step"));
		} catch (NumberFormatException e) {
			throw new LocoException(e, OfferParserCode1100.PREPROC_STEP_NOT_NUMBER);
		}
	}
}