package com.att.datalake.loco.offerconfiguration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * store all offer criteria related attributes
 * use criteriaID = "LOCO"
 * @author ac2211
 *
 */
@Entity(name = "loco_offer_criteria")
public class OfferCriteria {

	@Id
	String criteriaId;
	
	@CreatedDate
	Date created;

	@LastModifiedDate
	Date lastModified;
	
	/**
	 * table name for the final daily offer table
	 */
	@Column
	String offerDailyTable;
	
	@Column(length=20000)
	String offerCriteriaSql;
	
	@Column
	String localExtractDir;

	public String getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(String criteriaId) {
		this.criteriaId = criteriaId;
	}

	public String getOfferCriteriaSql() {
		return offerCriteriaSql;
	}

	public void setOfferCriteriaSql(String offerCriteriaSql) {
		this.offerCriteriaSql = offerCriteriaSql;
	}

	public String getLocalExtractDir() {
		return localExtractDir;
	}

	public void setLocalExtractDir(String localExtractDir) {
		this.localExtractDir = localExtractDir;
	}

	public String getOfferDailyTable() {
		return offerDailyTable;
	}

	public void setOfferDailyTable(String offerDailyTable) {
		this.offerDailyTable = offerDailyTable;
	}		
}