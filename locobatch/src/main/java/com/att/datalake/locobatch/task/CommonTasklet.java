package com.att.datalake.locobatch.task;

public interface CommonTasklet {

	void process();
	String getName();
	String getDescr();
}
