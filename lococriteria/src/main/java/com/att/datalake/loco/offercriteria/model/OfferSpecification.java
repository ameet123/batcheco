package com.att.datalake.loco.offercriteria.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * data structure class to communicate all the attributes of an offer
 * @author ac2211
 */
public class OfferSpecification {
	private String offerId;
	
	@SerializedName("detail")
	private List<Detail> details = new ArrayList<Detail>();

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public List<Detail> getDetails() {
		return details;
	}

	public void setDetails(List<Detail> details) {
		this.details = details;
	}
	/**
	 * inner class for details regarding offer, because each offer will have multiple records in the file
	 * each record outlining the specific criterion
	 * @author ac2211
	 */
	public static class Detail {
		private byte criterionId;
		private char criterionType;
		/**
		 * on which attributes are we going to apply this criterion
		 */
		private List<String> criterionApplyObject;
		/**
		 * appropriate and relevant values for each criterion
		 */
		private List<String> criterionValues;
		
		public byte getCriterionId() {
			return criterionId;
		}

		public void setCriterionId(byte criterionId) {
			this.criterionId = criterionId;
		}

		public char getCriterionType() {
			return criterionType;
		}

		public void setCriterionType(char criterionType) {
			this.criterionType = criterionType;
		}
		public List<String> getCriterionApplyObject() {
			return criterionApplyObject;
		}

		public void setCriterionApplyObject(List<String> criterionApplyObject) {
			this.criterionApplyObject = criterionApplyObject;
		}

		public List<String> getCriterionValues() {
			return criterionValues;
		}
		public void setCriterionValues(List<String> criterionValues) {
			this.criterionValues = criterionValues;
		}
	}
}