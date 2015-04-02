package com.att.datalake.loco.mrprocessor.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.apache.hadoop.hive.ql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link Driver#run()} sends its output to stdout. There does not appear to
 * be any API to get that output inside the java program. We want to capture a
 * few stats out of the output. So we need to divert this to a string/file so we
 * can extract relevant pieces. The approach is as follows,
 * {@link FilterOutputStream} is the class in Java which enables modification of
 * underlying {@link OutputStream}. Using this, we can override the
 * {@link OutputStream#write(byte[])} method and in that we can additionally
 * send the output to a stringbuilder. This way we can modify the outputstream
 * to additionally get a stringbuilder object with all the data emitting out of
 * hive query
 * 
 * @author ac2211
 *
 */
public class StringLoggerStream extends PrintStream {
	private static final Logger LOGGER = LoggerFactory.getLogger(StringLoggerStream.class);
	final StringBuilder buf;
	final PrintStream underlying;

	StringLoggerStream(StringBuilder sb, OutputStream os, PrintStream ul, String querylog) {
		super(os);
		this.buf = sb;
		this.underlying = ul;
		File file = new File(querylog);
		if (!file.exists()) {
			try {
				if (file.createNewFile()) {
					LOGGER.info("hive query log:{} created", querylog);
				}
			} catch (IOException e) {
				LOGGER.error("Error creating hive querylog:{}", querylog);
			}
		}
	}

	/**
	 * capture System.out or System.err and create an instance of this class
	 * which will provide a stringbuilder object as well as the original stream
	 * for resetting after the work has finished.
	 * Console output is suppressed via 3rd parameter
	 * @param toLog
	 * @return
	 */
	public static StringLoggerStream create(PrintStream toLog, String queryLog, boolean isSuppressConsole) {
		try {
			final StringBuilder sb = new StringBuilder();
			Field f = FilterOutputStream.class.getDeclaredField("out");
			f.setAccessible(true);
			OutputStream psout = (OutputStream) f.get(toLog);
			return new StringLoggerStream(sb, new FilterOutputStream(psout) {
				public void write(int b) throws IOException {
					if (!isSuppressConsole) {
						super.write(b);
					}
					try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(queryLog, true)))) {
						out.print((char)b);
					}
					sb.append((char) b);
				}
			}, toLog, queryLog);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException shouldNotHappen) {
			LOGGER.error("Error in writing to file or string:{}", shouldNotHappen.getMessage());
		}
		return null;
	}

	public PrintStream getUnderlying() {
		return underlying;
	}

	public StringBuilder getCaptured() {
		return buf;
	}
}