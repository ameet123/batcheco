package com.att.datalake.loco.offerconfiguration.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * store all offer related attributes
 * @author ac2211
 *
 */
@Entity(name = "loco_offer")
public class Offer {

	@Id
	String offerId;
	
	@CreatedDate
	Date created;

	@LastModifiedDate
	Date lastModified;
	
	@Column(length=20000)
	String offerCriteriaSql;
	
	@Column(length=20000)
	String offerPreProcSql;
	
	/**
	 * the criteria object {@link OfferSpecification} 
	 * which is turned into a JSON string
	 */
	@Column(length = 15000)
	String offerCriteria;
	/**
	 * how many components does this offer have, useful for triggering the final job
	 */
	int componentCount;
	/**
	 * we need bi-directional using mappedBy because we want to operate 
	 * on children thr' parent. we need EAGER, because otherwise 
	 * we can't fetch the children from parent
	 * deleteAll will not remove orphans
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="offer", orphanRemoval=true, fetch=FetchType.EAGER)
	List<OfferComponent> components = new ArrayList<OfferComponent>();

	/**
	 * @return the offerId
	 */
	public String getOfferId() {
		return offerId;
	}

	/**
	 * @param offerId the offerId to set
	 */
	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the lastModified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * @return the offerSql
	 */
	public String getOfferCriteriaSql() {
		return offerCriteriaSql;
	}

	/**
	 * @param offerSql the offerSql to set
	 */
	public void setOfferCriteriaSql(String offerSql) {
		this.offerCriteriaSql = offerSql;
	}

	/**
	 * @return the offerPreProcSql
	 */
	public String getOfferPreProcSql() {
		return offerPreProcSql;
	}

	/**
	 * @param offerPreProcSql the offerPreProcSql to set
	 */
	public void setOfferPreProcSql(String offerPreProcSql) {
		this.offerPreProcSql = offerPreProcSql;
	}

	/**
	 * @return the offerCriteria
	 */
	public String getOfferCriteria() {
		return offerCriteria;
	}

	/**
	 * @param offerCriteria the offerCriteria to set
	 */
	public void setOfferCriteria(String offerCriteria) {
		this.offerCriteria = offerCriteria;
	}

	/**
	 * @return the componentCount
	 */
	public int getComponentCount() {
		return componentCount;
	}

	/**
	 * @param componentCount the componentCount to set
	 */
	public void setComponentCount(int componentCount) {
		this.componentCount = componentCount;
	}

	/**
	 * @return the components
	 */
	public List<OfferComponent> getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(List<OfferComponent> components) {
		this.components = components;
	}
	public void addToComponents(OfferComponent component) {
		component.setOffer(this);
		this.components.add(component);
    }
}