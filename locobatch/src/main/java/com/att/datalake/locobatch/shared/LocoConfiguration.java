package com.att.datalake.locobatch.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.datalake.loco.preproc.model.PreProcSpec;

/**
 * a data sturcture class that we can use to store data from execution during
 * the running of a job
 * 
 * @author ac2211
 *
 */
@Component
public class LocoConfiguration {

	Map<String, RuntimeData> configMap;
	
	public LocoConfiguration() {
		configMap = new HashMap<String, LocoConfiguration.RuntimeData>();
	}
	
	public RuntimeData get(String offerId) {
		RuntimeData data = configMap.get(offerId);
		if (data == null) {
			data = new RuntimeData();
			configMap.put(offerId, data);
		}
		return data;
	}
	
	public void set(String offerId, RuntimeData data) {
		configMap.put(offerId, data);
	}
	public Set<String> offerIterator() {
		return configMap.keySet();
	}
	/**
	 * return a map of offer id => preprocessing sql
	 * @return
	 */
	public Map<String, String> getPreProcSqls() {
		Map<String, String> sqls = new HashMap<String, String>();
		for (Entry<String, RuntimeData> e: configMap.entrySet()) {
			if (!StringUtils.isEmpty(e.getValue().getPreProcSql())) {
				sqls.put(e.getKey(), e.getValue().getPreProcSql());
			}
		}
		return sqls;
	}
	/**
	 * # of entries packed 
	 * @return
	 */
	public int count() {
		return configMap.size();
	}
	
	public static class RuntimeData {
		private PreProcSpec offerSpec;
		private String preProcSql;
		private String criteriaSql;

		public PreProcSpec getOfferSpec() {
			return offerSpec;
		}

		public void setOfferSpec(PreProcSpec offerSpec) {
			this.offerSpec = offerSpec;
		}

		public String getPreProcSql() {
			return preProcSql;
		}

		public void setPreProcSql(String preProcSql) {
			this.preProcSql = preProcSql;
		}

		public String getCriteriaSql() {
			return criteriaSql;
		}

		public void setCriteriaSql(String criteriaSql) {
			this.criteriaSql = criteriaSql;
		}
	}
}