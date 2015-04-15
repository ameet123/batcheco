package com.att.datalake.loco.offerconfiguration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.att.datalake.loco.offerconfiguration.model.BatchJobExecution;

/**
 * to set up JPA crud methods against offer configuration table
 * @author ac2211
 *
 */
public interface JobExecutionRepository extends JpaRepository<BatchJobExecution, String> {
}
