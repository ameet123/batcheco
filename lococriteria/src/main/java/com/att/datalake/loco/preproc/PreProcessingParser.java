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
import org.springframework.util.StringUtils;

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

	private String preProcFile;
	private CSVFormat preProcFmt = CSVFormat.DEFAULT.withHeader("offerId", "step", "leftTable", "leftColumns",
			"rightTable", "rightColumns", "op", "opColumn", "matchingTable", "output").withSkipHeaderRecord(true);

	public List<PreProcSpec> parse() {
		List<PreProcSpec> preSpecList = new ArrayList<PreProcSpec>();
		// so that we can do a contains ? query in O(1) as opposed to O(n) ->
		// List
		Map<String, PreProcSpec> preProcMap = new HashMap<String, PreProcSpec>();

		if (StringUtils.isEmpty(preProcFile)) {
			throw new LocoException(OfferParserCode1100.NO_PREPROC_FILE_SET);
		}
		Iterable<CSVRecord> preProcRecords;
		try {
			preProcRecords = preProcFmt.parse(new FileReader(preProcFile));
		} catch (IOException e) {
			throw new LocoException(e, OfferParserCode1100.PREPROC_FILE_READ_ERROR);
		}
		// iterate over the records and start packing attributes into the Offer
		// object
		List<ProcDetail> details;
		ProcDetail d;
		for (CSVRecord record : preProcRecords) {

			PreProcSpec pr = preProcMap.get(record.get("offerId"));
			if (pr == null) {
				LOGGER.debug("Adding new offer:{}", record.get("offerId"));
				pr = new PreProcSpec();
				pr.setOfferId(record.get("offerId"));
				preProcMap.put(record.get("offerId"), pr);
				preSpecList.add(pr);
			}

			// set details
			details = pr.getProcDetail();
			d = new PreProcSpec.ProcDetail();
			try {
				d.setStep(Integer.parseInt(record.get("step")));
			} catch (NumberFormatException e) {
				throw new LocoException(e, OfferParserCode1100.PREPROC_STEP_NOT_NUMBER);
			}
			d.setLeftTable(record.get("leftTable"));
			d.setRightTable(record.get("rightTable"));
			d.setOutput(record.get("output"));
			char c = record.get("op").charAt(0);
			d.setOp(c);
			if (!Character.isLetter(c)
					|| ((c != PreProcOperation.JOIN.getValue()) && (c != PreProcOperation.UNION.getValue()))) {
				throw new LocoException(OfferParserCode1100.PREPROC_MERGE_OP_NOT_VALID);
			}
			d.setOpColumn(record.get("opColumn"));
			d.setMatchingTable(record.get("matchingTable"));

			// LISTS
			List<String> list = Utility.getStringList(record.get("leftColumns"));
			if (list == null || list.size() == 0) {
				throw new LocoException(OfferParserCode1100.LEFT_TABLE_COLUMNS_NOTEXIST);
			}
			d.setLeftColumns(list);

			list = Utility.getStringList(record.get("rightColumns"));
			if (list == null || list.size() == 0) {
				throw new LocoException(OfferParserCode1100.RIGHT_TABLE_COLUMNS_NOTEXIST);
			}
			d.setRightColumns(list);

			// add detail to list of details
			details.add(d);
		}
		return preSpecList;
	}

	/**
	 * @return the preProcFile
	 */
	public String getPreProcFile() {
		return preProcFile;
	}

	/**
	 * @param preProcFile
	 *            the preProcFile to set
	 */
	public void setPreProcFile(String preProcFile) {
		this.preProcFile = preProcFile;
	}
}