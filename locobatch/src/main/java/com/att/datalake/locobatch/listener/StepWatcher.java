package com.att.datalake.locobatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StepWatcher implements StepExecutionListener {

	@Override
	public ExitStatus afterStep(StepExecution exec) {
		System.out.println("Step:"+exec.getStepName()+" started:"+exec.getStartTime()+" end:"+exec.getEndTime()+" status:"+exec.getStatus().name());
		return null;
	}

	@Override
	public void beforeStep(StepExecution arg0) {
		// TODO Auto-generated method stub
		
	}

}
