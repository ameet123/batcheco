package com.att.datalake.loco.preproc.builder;

import java.util.Map;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;

/**
 * build a map of from clauses from the tables passed and the aliases passed
 * 
 * @author ac2211
 *
 */
public class FromMapBuilder {

	/**
	 * if the left alias is null, that means the left table is Transient
	 * we do not need to add it to from clause
	 * @param d
	 * @param fromMap
	 * @param rAlias
	 * @param lAlias
	 * @return
	 */
	public Map<String, String> build(PreProcSpec.ProcDetail d, Map<String, String> fromMap, String rAlias, String lAlias) {
		if (lAlias != null) {
			fromMap.put(d.getLeftTable(), lAlias);
		}
		fromMap.put(d.getRightTable(), rAlias);
		return fromMap;
	}
}