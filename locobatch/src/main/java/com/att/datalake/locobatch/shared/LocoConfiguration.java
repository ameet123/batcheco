package com.att.datalake.locobatch.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

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
	 * # of entries packed 
	 * @return
	 */
	public int count() {
		return configMap.size();
	}
	
	public static class RuntimeData {
		private PreProcSpec offerSpec;
		private String preProcSql;

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
	}
}