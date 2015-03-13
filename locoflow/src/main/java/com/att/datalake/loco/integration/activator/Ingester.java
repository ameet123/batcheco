package com.att.datalake.loco.integration.activator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.att.datalake.loco.exception.IngestionCode1800;
import com.att.datalake.loco.exception.LocoException;
import com.att.datalake.loco.integration.core.OfferMessage;
import com.att.datalake.loco.integration.hdfs.MoveToHdfs;

/**
 * a service activator to actually ingest files into the respoisitory,
 * in this case HDFS
 * @author ac2211
 *
 */
@Component
public class Ingester {
	private static final Logger LOGGER = LoggerFactory.getLogger(Ingester.class);
	
	private MoveToHdfs fileMover;
	
	@Autowired
	public Ingester(MoveToHdfs fileMover) {
		this.fileMover = fileMover;
	}
	
	public OfferMessage handle(Message<OfferMessage> msg) {
		// get hive directory path
		String remotePath = msg.getPayload().getOfferComponent().getHiveDirectory();
		String localFile = msg.getPayload().getOfferFile().getAbsolutePath();
		LOGGER.debug("Moving file {} to remote path:{}", localFile, remotePath);
		try {
			if (!fileMover.move(localFile, remotePath, true)) {
				throw new LocoException(IngestionCode1800.ERROR_IN_MOVING_FILE_TO_HDFS);		
			}
		} catch (Exception e) {
			throw new LocoException(e, IngestionCode1800.ERROR_IN_MOVING_FILE_TO_HDFS);			
		}
		return msg.getPayload();
	}
}