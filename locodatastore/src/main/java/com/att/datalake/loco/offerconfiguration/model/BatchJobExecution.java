package com.att.datalake.loco.offerconfiguration.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * store all offer related attributes
 * @author ac2211
 *
 */
@Entity(name = "BATCH_JOB_EXECUTION")
public class BatchJobExecution {

	@Id
	@Column(name="JOB_EXECUTION_ID")
	long jobExecutionId;
	
	@Column(name="VERSION")
	long version;
	
	@Column(name="JOB_INSTANCE_ID")
	String offerPreProcSql;
	
	@Column(name="CREATE_TIME")
	Timestamp createTime;
	
	@Column(name="START_TIME")
	Timestamp startTime;
	
	@Column(name="END_TIME")
	Timestamp endTime;
	
	@Column(name="STATUS", length=10)
	String status;
	
	@Column(name="EXIT_CODE", length=2500)
	String exitCode;
	
	@Column(name="EXIT_MESSAGE", length=2500)
	String exitMessage;
	
	@Column(name="LAST_UPDATED")
	Timestamp lastUpdated;
	
	@Column(name="JOB_CONFIGURATION_LOCATION", length=2500)
	String jobConfigurationLocation;

	public long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getOfferPreProcSql() {
		return offerPreProcSql;
	}

	public void setOfferPreProcSql(String offerPreProcSql) {
		this.offerPreProcSql = offerPreProcSql;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getJobConfigurationLocation() {
		return jobConfigurationLocation;
	}

	public void setJobConfigurationLocation(String jobConfigurationLocation) {
		this.jobConfigurationLocation = jobConfigurationLocation;
	}
}