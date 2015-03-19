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
import com.google.gson.Gson;

/**
 * a class to parse pre-processor file as a step task
 * 
 * @author ac2211
 *
 */
@Component
public class PreProcessorParserTasklet implements Tasklet {

	@Autowired
	private Gson gson;
	@Autowired
	private PreProcessingParser pr;

	public void setFile(String file) {
		pr.setPreProcFile(file);
	}

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		List<PreProcSpec> specs = pr.parse();
		for (PreProcSpec p : specs) {
			System.out.println(gson.toJson(p));
		}
		return RepeatStatus.FINISHED;
	}
}