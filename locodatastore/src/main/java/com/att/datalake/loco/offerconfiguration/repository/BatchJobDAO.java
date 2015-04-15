package com.att.datalake.loco.offerconfiguration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * implementation class for all db interactions to spring batch jobs
 * @author ac2211
 *
 */
@Component
public class BatchJobDAO {

	@Autowired
	private JobExecutionRepository jobExecRepo;
	
	public long countJobs() {
		return jobExecRepo.count();
	}
}