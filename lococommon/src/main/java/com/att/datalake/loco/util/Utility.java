package com.att.datalake.loco.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.datalake.loco.exception.ErrorCode;
import com.att.datalake.loco.exception.FileCode1400;
import com.att.datalake.loco.exception.LocoException;

public final class Utility {
	private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);

	private Utility() {
		// nothing here
	}

	/**
	 * concatenate source file to the target Files.asCharSink(new File(target),
	 * Charset.defaultCharset(), FileWriteMode.APPEND).writeFrom( new
	 * FileReader(src));
	 * 
	 * @param file1
	 * @param file2
	 */
	public static void concatenate(String src, String target) {
		try {
			BufferedReader in = Files.newBufferedReader(Paths.get(src));
			String line;
			while ((line = in.readLine()) != null) {
				FileUtils.write(new File(target), line + System.lineSeparator(), Charset.defaultCharset(), true);
			}
		} catch (IOException e) {
			throw new LocoException(e, FileCode1400.CONCATENATED_FILE_CREATION_ERROR);
		}
	}

	public static boolean mkdir(String dir) {
		return new File(dir).mkdirs();
	}

	/**
	 * pretty format SQL Generated using hibernate
	 */
	public static String prettyPrint(String sql) {
		return new BasicFormatterImpl().format(sql);
	}

	/**
	 * get a list of files from the output directory, and join them into one
	 * file
	 * @return 
	 * 
	 * @throws IOException
	 */
	public static boolean concatenateFiles(File[] files, String target) {
		for (File f : files) {
			transferFrom(f.getAbsolutePath(), target);
		}
		return outputCheck(target, FileCode1400.CONCATENATED_FILE_CREATION_ERROR);
	}

	/**
	 * given a directory and exclusion prefix, return an aphabetically sorted
	 * list of files from that directory not matching the prefix.
	 * 
	 * @param dir
	 * @param exclusionPrefix
	 * @return
	 */
	public static File[] listFiles(String dir, String exclusionPrefix) {
		File[] files = new File(dir).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(exclusionPrefix)) {
					return false;
				}
				return true;
			}
		});
		Arrays.sort(files);
		return files;
	}

	/**
	 * a faster method to append a file to another
	 * 
	 * @param in
	 * @param out
	 */
	public static void transferFrom(String in, String out) {
		File infile = new File(in);
		File outfile = new File(out);

		try (FileOutputStream outStream = new FileOutputStream(outfile, true);
				RandomAccessFile inRAF = new RandomAccessFile(infile, "r")) {
			FileChannel outChan = outStream.getChannel();
			FileChannel inChan = inRAF.getChannel();

			long startSize = outfile.length();
			long inFileSize = infile.length();
			long bytesWritten = 0;

			// set the position where we should start appending the data
			// and get that position
			long startByte = outChan.position(startSize).position();
			while (bytesWritten < inFileSize) {
				bytesWritten += outChan.transferFrom(inChan, startByte, inFileSize);
				startByte = bytesWritten + 1;
			}
		} catch (IOException e) {
			throw new LocoException(e, FileCode1400.CONCATENATE_FILE_ERROR);
		}
	}

	/**
	 * go through the extracted files and add them to compressed output
	 * 
	 * @return
	 */
	public static boolean compress(File[] files, String target) {
		try (GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(target))) {
			int bytesRead;
			byte[] buffer = new byte[1024];

			for (File f : files) {
				LOGGER.trace("Processing:{}", f);
				try (FileInputStream in = new FileInputStream(f)) {
					while (-1 != (bytesRead = in.read(buffer))) {
						gz.write(buffer, 0, bytesRead);
					}
				}
			}
		} catch (IOException e1) {
			throw new LocoException(e1, FileCode1400.COMPRESS_FILE_CREATION_ERROR);
		}
		return outputCheck(target, FileCode1400.COMPRESS_FILE_CREATION_ERROR);
	}

	private static boolean outputCheck(String file, ErrorCode error) {
		if (new File(file).isFile()) {
			return true;
		} else {
			throw new LocoException(error);
		}
	}

	public static int getLineCount(String file) {
		try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file))) {
			lineNumberReader.skip(Long.MAX_VALUE);
			return lineNumberReader.getLineNumber();
		} catch (FileNotFoundException e) {
			LOGGER.error("File {} for line count not found", file);
			return 0;
		} catch (IOException e) {
			LOGGER.error("Could not read the entire file {}", file);
			return 0;
		}
	}

	public static String pad(String str, int size, char padChar) {
		StringBuilder padded = new StringBuilder(str);
		while (padded.length() < size) {
			padded.append(padChar);
		}
		return padded.toString();
	}

	/**
	 * first remove any carriage return/line feed, then split on "," with
	 * optional spaces we need positive look ahead because we don't want to
	 * break up MOD(BAN, 1000) into multiple tokens (?= => positive look ahead (
	 * => start of look ahead pattern [^\\(] => anything but open parenthesis *
	 * => zero or more times : This is MOD before "(" \\( => match the open
	 * paren [^\\)] => anything but close paren * => zero or more times : This
	 * is BAN,1000 \\) => match the close paren * => repeat thus matched pattern
	 * zero or more times [^\\)]* => followed by anything but close paren
	 * multiple times. just to ensure that there is no other paren
	 * 
	 * @param s
	 * @return
	 */
	public static List<String> getStringList(String s) {
		String[] sArray = s.replaceAll("\\r\\n|\\r|\\n", " ").split(",\\s*(?=([^\\(]*\\([^\\)]*\\))*[^\\)]*$)");
		return Arrays.asList(sArray);
	}

	/**
	 * read a file from classpath of the class that is passed and return a
	 * string for all the content
	 * 
	 * @param filename
	 * @param klz
	 * @return
	 */
	public static String classpathFileToString(String filename, Class<?> klz) {
		URL url = klz.getClassLoader().getResource(filename);
		String s;
		try {
			s = FileUtils.readFileToString(new File(url.getPath()));
		} catch (IOException e) {
			throw new LocoException(e, FileCode1400.CLASSPATH_FILE_READ_ERROR);
		}
		return s;
	}
}