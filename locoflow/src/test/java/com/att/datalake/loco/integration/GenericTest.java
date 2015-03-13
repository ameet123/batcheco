package com.att.datalake.loco.integration;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;
import com.google.gson.Gson;
/**
 * generic class from where to extend
 * @author ac2211
 *
 */
public abstract class GenericTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private Gson gson;
	@Autowired
	MessageChannel pickedFilesChannel;
	@Autowired
	private OfferDAO offerDao;

	private String offerId = "ABCDEFGHIJ1234567893";
	private final String COMP1_DIR = "/home/ac2211/Loco/offer1/charge";
	private final String COMP2_DIR = "/home/ac2211/Loco/offer1/abc";
	private final String FILE1 = COMP1_DIR+"/charge1.dat";
	private final String FILE2 = COMP2_DIR+"/abc1.dat";
	
	

	@BeforeClass
	public void init() {
		Offer o = new Offer();
		o.setOfferId(offerId);
		
		OfferComponent oc = new OfferComponent();
		oc.setComponentName("charge");
		oc.setHiveDb("ameet");
		oc.setHiveDirectory("/user/ac2211/ameet.db/loco_charge");
		oc.setHiveTable("loco_charge");
		oc.setLocalDirectory(COMP1_DIR);
		oc.setLocalArchiveDirectory("/home/ac2211/Archive/Loco/offer1/charge");
		oc.setLocalArchiveRetentionDays(10);
		o.addToComponents(oc);
		
		oc = new OfferComponent();
		oc.setComponentName("abc");
		oc.setHiveDb("ameet");
		oc.setHiveDirectory("/user/ac2211/ameet.db/loco_abc");
		oc.setHiveTable("loco_abc");
		oc.setLocalDirectory(COMP2_DIR);
		oc.setLocalArchiveDirectory("/home/ac2211/Archive/Loco/offer1/abc");
		oc.setLocalArchiveRetentionDays(10);
		o.addToComponents(oc);
				
		offerDao.saveOffer(o);
		// create dirs
		new File(COMP1_DIR).mkdirs();
		new File(COMP2_DIR).mkdirs();
		try {
			new File(FILE1).createNewFile();
			new File(FILE2).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.state(offerDao.countOffers()==1);
		Assert.state(offerDao.countComponents()==2);
		oc = offerDao.findByDirectory(COMP1_DIR);
		Assert.state(oc!=null);
		System.out.println("---- OC Found from DB:"+oc.getComponentName());
	}
	
	@AfterClass
	public void post() {
		offerDao.deleteOffer(offerId);
		new File(FILE1).delete();
		new File(FILE2).delete();
	}
}