package com.att.datalake.loco.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.att.datalake.loco.batch.job.LocoMrOnlyJob;
import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.shared.LocoConfiguration.RuntimeData;

/**
 * here we actually create and spawn a specific job
 * @author ac2211
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	private LocoConfiguration config;
	@Bean
	public Job getJob() {
		RuntimeData data = new RuntimeData();
		data.setPreProcSql("select count(*) from ameet.loco_offer1");
		config.set("a123", data);
		System.out.println("Running the test");
		return new LocoMrOnlyJob().preProcessingJob();
	}
}
