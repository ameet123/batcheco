package com.att.datalake.loco.preproc.builder.step;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.model.PreProcProcessorData;
import com.att.datalake.loco.preproc.model.PreProcTabularData;

@Component
public class SelectColMapBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SelectColMapBuilder.class);

	/**
	 * pattern for matching a select clause function such as MOD(BAN,1000)
	 */
	private final String FUNC_MATCH_REGEX = "(.*?)\\(([\\w]+)(.*)\\)(.*)";
	private Pattern functionPattern = Pattern.compile(FUNC_MATCH_REGEX);

	/**
	 * given two sets of columns, generate a unique map with appropriate alias
	 * removing duplicates. We first get the left columns. Assumption is that
	 * left table is a superset or more closer to final look we also have to
	 * account for function such as MOD(BAN,1000)
	 * 
	 * @param d
	 * @param selectMap
	 * @param rightColumns
	 * @param rAlias
	 * @param leftColumns
	 * @param lAlias
	 * @return
	 */
	public Map<String, String> build(PreProcProcessorData processorDTO) {
		PreProcTabularData tabularData = processorDTO.getTabularData();
		// process left if needed
		if (tabularData.getLeftColumns() != null) {
			for (String c : tabularData.getLeftColumns()) {
				processColumnAndAdd(c, tabularData.getLeftAlias(), processorDTO.getCurrentSelectMap());
			}
		}
		// do right, the key being, if we find the column already in map, skip
		for (String c : tabularData.getRightColumns()) {
			if (c.contains(" ")) {
				LOGGER.debug("checking alias of right col:{}", c.split("\\s+")[1]);
				if (processorDTO.getCurrentSelectMap().containsKey(c.split("\\s+")[1])) {
					continue;
				}
			} else {
				if (processorDTO.getCurrentSelectMap().containsKey(c)) {
					continue;
				}
			}
			processColumnAndAdd(c, tabularData.getRightAlias(), processorDTO.getCurrentSelectMap());
		}
		return processorDTO.getCurrentSelectMap();
	}

	/**
	 * sometimes the column may contain a function MOD(BAN,1000) in this case,
	 * we want to add the alias to the column right here and then tell the
	 * sqlbuilder to not add any alias by passing null
	 * 
	 * @param col
	 * @param alias
	 * @return
	 */
	private void processColumnAndAdd(String col, String alias, Map<String, String> map) {
		String processedCol;
		boolean nullAlias = false;
		if (col.contains("(")) {
			LOGGER.debug("Checking col:{} contains open paren", col);
			Matcher m = functionPattern.matcher(col);
			if (m.find()) {
				LOGGER.debug("Processing col:{}, match found for ) ", col);
				StringBuilder sb = new StringBuilder();
				sb.append(m.group(1)).append("(").append(alias).append(".").append(m.group(2)).append(m.group(3))
						.append(")").append(m.group(4));
				LOGGER.debug("function column processed:{}", sb.toString());
				processedCol = sb.toString();
				nullAlias = true;
			} else {
				processedCol = col;
			}
		} else {
			processedCol = col;
		}
		map.put(processedCol, (nullAlias) ? null : alias);
	}
}