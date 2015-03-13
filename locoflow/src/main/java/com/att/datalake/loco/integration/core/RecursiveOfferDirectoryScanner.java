package com.att.datalake.loco.integration.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.stereotype.Component;

/**
 * custom scanner using
 * {@link Files#walk(Path, java.nio.file.FileVisitOption...)} to pick up offer
 * files. we want to sort them by last modified; additionally we would like to
 * use regex in future or depth first walk from NIO. consequently, we have our
 * own implementation.
 * 
 * @author ac2211
 *
 */
@Component
public class RecursiveOfferDirectoryScanner extends DefaultDirectoryScanner {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecursiveOfferDirectoryScanner.class);
	/**
	 * we are assuming that the dir structure is : Loco/offer1/comp1,
	 * Loco/offer1/comp2, ... so we need 3 levels deep scanning
	 */
	private static final int TRAVERSE_DEPTH = 3;

	protected File[] listEligibleFiles(File directory) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Scanning:{}", directory.getAbsolutePath());
			File[] files = dirWalk(directory.toPath());
			for (File f: files) {
				LOGGER.trace("File picked:{}",f.getAbsolutePath());
			}
			return files;
		}
		return dirWalk(directory.toPath());
	}

	/**
	 * get files at level 2 sorted by last modified timestamp we are assuming
	 * that the top level directory will have offer sub-directories with files
	 * in them
	 * 
	 * @param dir
	 * @return
	 */
	private File[] dirWalk(Path dir) {
		try {
			return Files.walk(dir, TRAVERSE_DEPTH).map(p -> p.toFile()).filter(p -> p.isFile())
					.sorted((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
					.toArray(size -> new File[size]);
		} catch (IOException e) {
			return new File[0];
		}
	}
}