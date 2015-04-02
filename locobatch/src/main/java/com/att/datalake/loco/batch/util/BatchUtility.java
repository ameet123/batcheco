package com.att.datalake.loco.batch.util;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class BatchUtility {

	private BatchUtility() {
		//
	}

	/**
	 * get a unique file to write hive query logs to
	 * @param context
	 * @return
	 */
	public static String getHiveLogFile(ChunkContext context) {
		return FilenameUtils.concat(getLogDirectory(), getUniqueJobId(context));
	}
	public static String getLogDirectory() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		String logfile = ((FileAppender<ILoggingEvent>) loggerContext.getLogger("ROOT").getAppender("FILE")).getFile();
		String hiveLogDir = FilenameUtils.concat(Paths.get(logfile).getName(0).toString(), "hive");
		// create if not exists
		new File(hiveLogDir).mkdir();
		return hiveLogDir;
	}

	public static String getUniqueJobId(ChunkContext context) {
		String separator = "__";
		long jobInstance = context.getStepContext().getStepExecution().getJobExecution().getJobInstance()
				.getInstanceId();
		String jobname = context.getStepContext().getJobName();
		String stepname = context.getStepContext().getStepName();
		Date start = context.getStepContext().getStepExecution().getStartTime();
		SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss");
		String startTime = sdf.format(start);
		StringBuilder sb = new StringBuilder("hive");
		sb.append(separator);
		sb.append(jobname);
		sb.append(separator);
		sb.append(jobInstance);
		sb.append(separator);
		sb.append(stepname);
		sb.append(separator);
		sb.append(startTime);
		sb.append(".log");
		return safe(sb.toString());
	}
	private static String safe(String s) {
		return s.replaceAll("[\\s:]", "_");
	}
}