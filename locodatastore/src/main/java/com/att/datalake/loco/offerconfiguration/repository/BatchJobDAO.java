package com.att.datalake.loco.offerconfiguration.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.offerconfiguration.model.BatchJobExecution;

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
	
	public List<Map<String, String>> allJobs() {
		List<Map<String, String>> rows = new ArrayList<Map<String,String>>();
		// convert to a map
		Map<String, String> rowMap;
		
		for (BatchJobExecution r: jobExecRepo.findAll()) {
			rowMap = new HashMap<String, String>();
			rowMap.put("jobExecutionId", String.valueOf(r.getJobExecutionId()));
			rowMap.put("startTime", String.valueOf(r.getStartTime()));
			rowMap.put("endTime", String.valueOf(r.getEndTime()));
			rowMap.put("status", r.getStatus());
			rows.add(rowMap);
		}
		return rows;
	}
}