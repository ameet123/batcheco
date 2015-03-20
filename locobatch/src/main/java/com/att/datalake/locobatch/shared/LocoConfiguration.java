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
			data.setOfferId(offerId);
		}
		return data;
	}
	public Set<String> offerIterator() {
		return configMap.keySet();
	}
	public void set(String offerId, RuntimeData data) {
		configMap.put(offerId, data);
	}
	
	public static class RuntimeData {
		private String offerId;
		private PreProcSpec offerSpec;
		private String preProcSql;

		public String getOfferId() {
			return offerId;
		}

		public void setOfferId(String offerId) {
			this.offerId = offerId;
		}

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