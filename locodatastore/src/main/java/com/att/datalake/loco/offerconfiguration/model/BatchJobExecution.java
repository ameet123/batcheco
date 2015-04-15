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
}