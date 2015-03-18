package com.att.datalake.loco.offercriteria.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * data structure to encapsulate the outputs of the {@link PreProcProcessorData}
 * essentially, we want to present a simpler, cleaner object to the client from
 * where it can get select, from and predicate maps
 * adding unionList here also allows us to encapsulate it from the {@link PreProcProcessorData} class
 * 
 * 
 * @author ac2211
 *
 */
public class PreProcOutputData {
	
	private List<String> unionItems;
	private List<OutputDetailData> detailData = new ArrayList<PreProcOutputData.OutputDetailData>();
	
	public void addOutputDetail(OutputDetailData d) {
		detailData.add(d);
	}
	
	public List<String> getUnionItems() {
		return unionItems;
	}

	public void setUnionItems(List<String> unionItems) {
		this.unionItems = unionItems;
	}

	public List<OutputDetailData> getDetailData() {
		return detailData;
	}

	public void setDetailData(List<OutputDetailData> detailData) {
		this.detailData = detailData;
	}

	public static class OutputDetailData {
		private Map<String, String> selectMap;

		private Map<String, String> fromMap;

		private Map<String, String> predicateMap;

		private String tableKey;

		public Map<String, String> getSelectMap() {
			return selectMap;
		}

		public void setSelectMap(Map<String, String> selectMap) {
			this.selectMap = selectMap;
		}

		public Map<String, String> getFromMap() {
			return fromMap;
		}

		public void setFromMap(Map<String, String> fromMap) {
			this.fromMap = fromMap;
		}

		public Map<String, String> getPredicateMap() {
			return predicateMap;
		}

		public void setPredicateMap(Map<String, String> predicateMap) {
			this.predicateMap = predicateMap;
		}

		public String getTableKey() {
			return tableKey;
		}

		public void setTableKey(String tableKey) {
			this.tableKey = tableKey;
		}
	}
}