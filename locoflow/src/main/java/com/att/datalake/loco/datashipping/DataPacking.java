package com.att.datalake.loco.datashipping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.FileCode1400;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.util.ExtractConstants;
import com.att.datalake.loco.util.Utility;

/**
 * This is to collect all files extracted from hadoop into a single file and
 * compress it.
 * 
 * @author ac2211
 *
 */
@Component
public class DataPacking {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataPacking.class);

	/**
	 * create shipping directory if needed
	 */
	public DataPacking() {
		if (Utility.mkdir(ExtractConstants.OUTPUT_DATA_SHIP_DIR)) {
			LOGGER.info("Output shipping dir created successfully:{}", ExtractConstants.OUTPUT_DATA_LANDING_DIR);
		}
		// clean up that dir
		new File(ExtractConstants.OUTPUT_DATA_FILE).delete();
	}

	/**
	 * concatenate all files in the extracted directory into a single file
	 * 
	 * @param should
	 *            we compress the final file
	 */
	public void pack(boolean isCompress) {
		boolean status;
		// the extract process creates a .crc file, which we want to avoid
		File[] files = Utility.listFiles(ExtractConstants.OUTPUT_DATA_LANDING_DIR, ".");
		status = concatenate(files);
		// get line count
		LOGGER.info("Final output file records # {}", Utility.getLineCount(ExtractConstants.OUTPUT_DATA_FILE));

		if (isCompress) {
			files = Utility.listFiles(ExtractConstants.OUTPUT_DATA_LANDING_DIR, ".");
			status = compress(files);
		}

		if (status) {
			LOGGER.info("Output file created successfully");
		} else {
			LOGGER.error("Error creating final output in compressed form");
			status = false;
		}
	}

	/**
	 * get a list of files from the output directory, sort it alphabetically and
	 * join them into one file NOT WORKING due to GUAVA JAR ISSUE
	 * 
	 * @throws IOException
	 */
	private boolean concatenate(File[] files) {
		for (File f : files) {
			LOGGER.debug("Concatenating {}", f);
			Utility.concatenate(f.getAbsolutePath(), ExtractConstants.OUTPUT_DATA_FILE);
		}
		return outputCheck(false);
	}

	/**
	 * go through the extracted files and add them to compressed output
	 * 
	 * @return
	 */
	private boolean compress(File[] files) {
		try (GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(ExtractConstants.OUTPUT_GZ_DATA_FILE))) {
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
		return outputCheck(true);
	}

	private boolean outputCheck(boolean isCompress) {
		if (isCompress) {
			if (new File(ExtractConstants.OUTPUT_GZ_DATA_FILE).isFile()) {
				return true;
			} else {
				throw new LocoException(FileCode1400.COMPRESS_FILE_CREATION_ERROR);
			}
		} else {
			if (new File(ExtractConstants.OUTPUT_DATA_FILE).isFile()) {
				return true;
			} else {
				throw new LocoException(FileCode1400.CONCATENATED_FILE_CREATION_ERROR);
			}
		}
	}
}