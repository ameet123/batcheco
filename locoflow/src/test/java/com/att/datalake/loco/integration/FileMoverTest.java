package com.att.datalake.loco.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.util.Assert;
import org.testng.annotations.Test;

import com.att.datalake.loco.SIContext2Config;
import com.att.datalake.loco.integration.core.OfferMessage;
import com.att.datalake.loco.integration.core.RecursiveOfferDirectoryScanner;
/**
 * tests the file pickup where we try to pick up files 
 * using {@link RecursiveOfferDirectoryScanner} and then try to match them
 * to an offer based on the component local directory.
 * //		MoveToHdfs mover  = Mockito.mock(MoveToHdfs.class);
	//		Mockito.when(mover.move(anyString(), anyString(), anyBoolean())).thenReturn(true);
 * @author ac2211
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { SIContext2Config.class })
public class FileMoverTest extends GenericTest {

	@Autowired
	@Qualifier("hdfsReadyChannel")
	PollableChannel hdfsReadyChannel;
	
	@Test
	public void testFileMover() throws InterruptedException {
		Thread.sleep(1000);
		int total = 2;
		int n = 0;
		for (int i = 1; i <= total; i++) {
			@SuppressWarnings("unchecked")
			Message<OfferMessage> msg = (Message<OfferMessage>) hdfsReadyChannel.receive(2000);
			System.out.println("Message # " + i + " received:" + msg.getPayload().getOfferFile().getAbsolutePath());
			n++;
		}
		Assert.state(n == total);
	}
}