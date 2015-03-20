package com.att.datalake.locobatch.task;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.preproc.PreProcessingParser;
import com.att.datalake.loco.preproc.model.PreProcSpec;
import com.att.datalake.locobatch.shared.LocoConfiguration;
import com.att.datalake.locobatch.shared.LocoConfiguration.RuntimeData;
import com.google.gson.Gson;

/**
 * a class to parse pre-processor file as a step task
 * parse the file and store data into the {@link LocoConfiguration} map
 * @author ac2211
 *
 */
@Component
public class PreProcessorParserTasklet implements Tasklet {

	@Autowired
	private Gson gson;
	@Autowired
	private PreProcessingParser pr;
	@Autowired
	private LocoConfiguration config;

	public void setFile(String file) {
		pr.setPreProcFile(file);
	}

	@Override
	public RepeatStatus execute(StepContribution contrib, ChunkContext context) throws Exception {
		List<PreProcSpec> specs = pr.parse();
		for (PreProcSpec p : specs) {
			System.out.println(gson.toJson(p));
			RuntimeData data = config.get(p.getOfferId());
			data.setOfferSpec(p);
		}		
		return RepeatStatus.FINISHED;
	}
}