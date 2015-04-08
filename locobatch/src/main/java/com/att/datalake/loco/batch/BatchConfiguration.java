package com.att.datalake.loco.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.shared.LocoConfiguration.RuntimeData;
import com.att.datalake.loco.batch.step.Step0;
import com.att.datalake.loco.batch.step.Step1;
import com.att.datalake.loco.batch.step.Step11;
import com.att.datalake.loco.batch.step.Step12;
import com.att.datalake.loco.batch.step.Step13;
import com.att.datalake.loco.batch.step.Step20;
import com.att.datalake.loco.batch.step.Step21;

/**
 * here we actually create and spawn a specific job
 * Since this is a component, we just make the specific job configuraiton we want
 * into a bean so that it gets launched
 * @author ac2211
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchConfiguration.class);
	
	@Autowired
	private LocoConfiguration config;
	@Autowired
	private JobBuilderFactory jobBuilders;
	@Autowired
	private Step0 step0;
	@Autowired
	private Step1 step1;
	@Autowired
	private Step11 step11;
	@Autowired
	private Step12 step12;
	@Autowired
	private Step13 step13;
	@Autowired
	private Step20 step20;
	@Autowired
	private Step21 step21;
	
	@Value("${offer.file:input/offer.csv}")
	private String filename;
	@Value("${job:mrtest}")
	private String jobname;
	/**
	 * load a job based on VM parameter -Djob or the default
	 * @return
	 */
	@Bean
	public Job getSpecifiedJob() {
		Job j;
		switch (jobname) {
		case "complete":
			j = completeLoco();
			break;
		case "preprocessing":
			j = preProcessingJob();
			break;
		case "criteria":
			j = offerCriteriaJob();
			break;
		default:
			j = getMrTestingJob();
		}
		LOGGER.info("Launching {} job", jobname);
		return j;
	}
	/**
	 * complete loco processing
	 * @return
	 */
	public Job completeLoco() {
		return jobBuilders.get("Job:Loco Processing").incrementer(new RunIdIncrementer()).
		// offer criteria load, process
		flow(step0.setFilename(filename).build()).
		next(step1.build()).
		next(step11.build()).
		// pre processing
		next(step12.build()).
		next(step13.build()).
		// Hive preprocessor logic
		next(step20.build()).
		next(step21.build()).
		end().
		build();
	}
	/**
	 * this is primarily to test hadoop connectivity
	 * @return
	 */
	public Job getMrTestingJob() {
		RuntimeData data = new RuntimeData();
		data.setPreProcSql("select count(*) from ameet.loco_offer1");
		config.set("a123", data);
		
		return jobBuilders.get("Job:Pre-Processing sql hive execution job").incrementer(new RunIdIncrementer()).
		flow(step20.build()).				
		end().
		build();
	}	
	/**
	 * this tests the pre-processing logic
	 * @return
	 */
	public Job preProcessingJob() {
		return jobBuilders.get("Job:Preprocessing parser job").incrementer(new RunIdIncrementer()).
				flow(step11.build()).
				next(step12.build()).
				next(step13.build()).
				end().
				build();
	}	
	/**
	 * this is just offer criteria parsing logic
	 * @return
	 */
	public Job offerCriteriaJob() {
		return jobBuilders.get("Job:Offer Criteria Processing job").incrementer(new RunIdIncrementer()).
				flow(step0.setFilename(filename).build()).
				next(step1.build()).
				end().
				build();
	}
}