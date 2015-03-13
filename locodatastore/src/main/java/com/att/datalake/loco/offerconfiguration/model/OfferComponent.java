package com.att.datalake.loco.offerconfiguration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * describe the file components of an offer. an offer is made up of one or more
 * files, we need to describe which files and details on where to move them.
 * each component of an offer will have a unique sub-directory, because that's
 * the way we can distinguish the component. In absence of that, we will have to
 * use name based distinction which can be errorprone.
 * 
 * @author ac2211
 */
@Entity(name = "loco_offer_component")
@Table(indexes = { @Index(name = "IDX__off_comp__localDir", columnList = "local_directory", unique = true) })
@SequenceGenerator(name = "idSeqGen", sequenceName = "LOCO_OFFER_COMP_SEQ")
public class OfferComponent {

	@Id
	@GeneratedValue(generator = "idSeqGen", strategy = GenerationType.AUTO)
	Long componentId;

	@CreatedDate
	Date created;

	@LastModifiedDate
	Date lastModified;

	/**
	 * a name to identify this record with
	 */
	String componentName;

	String hiveDb;

	String hiveTable;
	/**
	 * which HDFS directory to move files belonging to this component
	 */
	String hiveDirectory;
	/**
	 * linux directory in which files are received for this component files from
	 * here are removed immediately after processing
	 * the @Column annotation is needed just for the unique index
	 */
	@Column(name="local_directory")
	String localDirectory;
	/**
	 * files are kept here for a certain period of time as archive
	 */
	String localArchiveDirectory;
	/**
	 * # of days files should be kept in local archive area
	 */
	int localArchiveRetentionDays;

	@ManyToOne
	@JoinColumn(name = "offer_id")
	private Offer offer;

	/**
	 * @return the componentId
	 */
	public Long getComponentId() {
		return componentId;
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
	 * @return the componentName
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * @param componentName
	 *            the componentName to set
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	/**
	 * @return the hiveDb
	 */
	public String getHiveDb() {
		return hiveDb;
	}

	/**
	 * @param hiveDb
	 *            the hiveDb to set
	 */
	public void setHiveDb(String hiveDb) {
		this.hiveDb = hiveDb;
	}

	/**
	 * @return the hiveTable
	 */
	public String getHiveTable() {
		return hiveTable;
	}

	/**
	 * @param hiveTable
	 *            the hiveTable to set
	 */
	public void setHiveTable(String hiveTable) {
		this.hiveTable = hiveTable;
	}

	/**
	 * @return the hiveDirectory
	 */
	public String getHiveDirectory() {
		return hiveDirectory;
	}

	/**
	 * @param hiveDirectory
	 *            the hiveDirectory to set
	 */
	public void setHiveDirectory(String hiveDirectory) {
		this.hiveDirectory = hiveDirectory;
	}

	/**
	 * @return the localDirectory
	 */
	public String getLocalDirectory() {
		return localDirectory;
	}

	/**
	 * @param localDirectory
	 *            the localDirectory to set
	 */
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}

	/**
	 * @return the localArchiveDirectory
	 */
	public String getLocalArchiveDirectory() {
		return localArchiveDirectory;
	}

	/**
	 * @param localArchiveDirectory
	 *            the localArchiveDirectory to set
	 */
	public void setLocalArchiveDirectory(String localArchiveDirectory) {
		this.localArchiveDirectory = localArchiveDirectory;
	}

	/**
	 * @return the localArchiveRetentionDays
	 */
	public int getLocalArchiveRetentionDays() {
		return localArchiveRetentionDays;
	}

	/**
	 * @param localArchiveRetentionDays
	 *            the localArchiveRetentionDays to set
	 */
	public void setLocalArchiveRetentionDays(int localArchiveRetentionDays) {
		this.localArchiveRetentionDays = localArchiveRetentionDays;
	}

	/**
	 * @return the offer
	 */
	public Offer getOffer() {
		return offer;
	}

	/**
	 * @param offer
	 *            the offer to set
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}
}
