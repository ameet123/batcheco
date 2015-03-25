package com.att.datalake.loco.integration.hdfs;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.fs.FsShell;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.IngestionCode1800;
import com.att.datalake.loco.exception.LocoException;

/**
 * specific component to perform HDFS move it should only be repsonsible for
 * file movement to HDFS and not much else
 * 
 * @author ac2211
 *
 */
@Component
public class MoveToHdfs {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoveToHdfs.class);

	private final FsShell fsh;

	/**
	 * autowiring constructors let us have mock objects that we can pass here.
	 * 
	 * @param fsh
	 */
	@Autowired
	public MoveToHdfs(FsShell fsh) {
		this.fsh = fsh;
	}

	/**
	 * if the file is non-existent, false is returned
	 * 
	 * @param localFile
	 *            file name on local machine to copy
	 * @param remotePath
	 *            is a directory in which the file needs to be copied
	 * @return
	 */
	public boolean move(String localFile, String remotePath, boolean replace) {
		// if local file does not exist, just go back
		if (!new File(localFile).isFile()) {
			throw new LocoException(IngestionCode1800.LOCA_FILE_MISSING);
		}

		String remote = FilenameUtils.concat(remotePath, new File(localFile).getName());
		if (replace && fsh.test(remote)) {
			LOGGER.debug("file {} already exists in hdfs, deleting it", remote);
			fsh.rm(remote);
		}
		fsh.put(localFile, remotePath);
		return true;
	}
}