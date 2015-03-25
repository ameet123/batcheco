package com.att.datalake.loco.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.util.Utility;

@Component
public class GenericStepMonitor implements StepExecutionListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericStepMonitor.class);
	
	private String marker;
	public GenericStepMonitor() {
		marker = Utility.pad(" ", 80, '=');
	}

	@Override
	public void beforeStep(StepExecution exec) {
		String stepName = exec.getStepName();

		StringBuilder sb = new StringBuilder();
		sb.append(marker);
		sb.append("\n\n");
		sb.append("NAME:");
		sb.append(stepName);	
		sb.append("\n\n");
		
		System.out.println(sb.toString());
		LOGGER.debug(sb.toString());
	}

	@Override
	public ExitStatus afterStep(StepExecution exec) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(exec.getStepName());
		sb.append(" started:"+exec.getStartTime()+" status:"+exec.getStatus().name());
		sb.append("\n");
		sb.append(marker);
		sb.append("\n");
		System.out.println(sb.toString());
		return exec.getExitStatus();
	}
}