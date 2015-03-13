package com.att.datalake.loco.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import com.att.datalake.loco.SIContextConfig;
import com.att.datalake.loco.integration.core.OfferMessage;
import com.att.datalake.loco.integration.core.RecursiveOfferDirectoryScanner;
/**
 * tests the file pickup where we try to pick up files 
 * using {@link RecursiveOfferDirectoryScanner} and then try to match them
 * to an offer based on the component local directory.
 * @author ac2211
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { SIContextConfig.class })
public class DerivedFilePickupTest extends GenericTest {

	@Autowired
	PollableChannel readyFilesChannel;
	@Test
	public void testFilePickupChannel() throws InterruptedException {
		// we need this so that the flow can continue before check the receive method
		Thread.sleep(1000);
		int total = 2;
		int n = 0;
		for (int i = 1; i <= total; i++) {
			@SuppressWarnings("unchecked")
			Message<OfferMessage> msg = (Message<OfferMessage>) readyFilesChannel.receive(2000);
			System.out.println("Message # " + i + " received:" + msg.getPayload().getOfferFile().getAbsolutePath());
			n++;
		}
		Assert.state(n == total);
	}
}