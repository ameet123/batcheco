package com.att.datalake.loco.offerconfiguration.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.ContextConfig;
import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.model.OfferComponent;

/**
 * test db interactions using jpa
 * @author ac2211
 *
 */
@ContextConfiguration(	loader = AnnotationConfigContextLoader.class,
classes = { ContextConfig.class})
public class OfferDBTest extends AbstractTestNGSpringContextTests {
	
	private Offer saved;

	@Autowired
	private OfferDAO offerDao;
	
	private String offerId = "ABCDEFGHIJ1234567893";
	private final String LOCAL_DIR = "/home/ac2211/Loco/offer1/charge";
	private final String CRITERIA = "{\"offerId\":\"ABCDEFGHIJ1234567893\",\"detail\":[{\"criterionId\":1,\"criterionType\":\"E\",\"criterionApplyObject\":[\"feature_group\"],\"criterionValues\":[\"TF\",\"VF\",\"PF\"]},{\"criterionId\":2,\"criterionType\":\"E\",\"criterionApplyObject\":[\"FEATURE_CODE\"],\"criterionValues\":[\"AMBMIN\",\"AMBREV\",\"CVAB\",\"CVAT\",\"CVBK\",\"CVCL\",\"CVDP\",\"CVIC\",\"CVMT\",\"CVOC\",\"CVRM\",\"CVTL\",\"DCK\",\"DEPOST\",\"GENDEP\",\"LTPYM\",\"LTPYMM\",\"REFUND\",\"RFNDEP\",\"RSLTFE\",\"TLRNCE\",\"XMLPA\",\"PAFM\"]},{\"criterionId\":3,\"criterionType\":\"A\",\"criterionApplyObject\":[\"BAN\",\"SUBSCRIBER_NO\",\"FINANCE_EFF_DATE\",\"PARTITION_KEY\",\"ACTV_BILL_SEQ_NO\",\"ACTV_DATE\",\"FEATURE_CODE\",\"FTR_REVENUE_CODE\",\"SOC\",\"FEATURE_GROUP\",\"FEATURE_DESC\",\"FTR_MED_DESC\"],\"criterionValues\":[\"SUM(ACTV_AMT)\"]},{\"criterionId\":4,\"criterionType\":\"R\",\"criterionApplyObject\":[\"POINTS\"],\"criterionValues\":[\"ROUND(ACTV_AMT)\"]}]}";


	@Test()
	public void testOfferSave() {
		Offer o = new Offer();
		o.setOfferId(offerId);

		o.setOfferCriteria(CRITERIA);
		OfferComponent oc = new OfferComponent();
		oc.setComponentName("charge");
		oc.setHiveDb("ameet");
		oc.setHiveDirectory("/user/ac2211/ameet.db/loco_charge");
		oc.setHiveTable("loco_charge");
		oc.setLocalDirectory(LOCAL_DIR);
		oc.setLocalArchiveDirectory("/home/ac2211/Archive/Loco/offer1/charge");
		oc.setLocalArchiveRetentionDays(10);
		
		o.addToComponents(oc);
		
		saved = offerDao.saveOffer(o);
		Assert.state(saved.getOfferId().equals(offerId));
		Assert.state(saved.getComponents().size()==1);
	}
	@Test(dependsOnMethods = {"testOfferSave"})
	public void testFind() {
		Offer o = offerDao.findByOfferId(offerId);
		System.out.println("fetch:"+o.getOfferId());
		System.out.println("size of children:"+o.getComponents().size());
	}
	@Test(dependsOnMethods = {"testFind"})
	public void testFindByDir() {
		OfferComponent oc = offerDao.findByDirectory(LOCAL_DIR);
		// try to get offer
		System.out.println("Offer from component:"+oc.getOffer().getOfferId());
		Assert.state(oc != null);
	}
	@Test(dependsOnMethods = {"testFindByDir"})
	public void testFindNonExistentByDir() {
		OfferComponent oc = offerDao.findByDirectory(LOCAL_DIR+"_1");
		Assert.state(oc == null);
	}
	
	@Test(dependsOnMethods = {"testFindNonExistentByDir"})
	public void testFindAndUpdate() {
		Offer o = offerDao.findByOfferId(offerId);
		System.out.println("Original criteria:"+o.getOfferCriteria());
		// change something
		o.setOfferCriteria("XXX");
		o = offerDao.saveOffer(o);
		Assert.state(o.getOfferCriteria().equals("XXX"));
		// reset it to original
		o.setOfferCriteria(CRITERIA);
		o = offerDao.saveOffer(o);
		Assert.state(o.getOfferCriteria().equals(CRITERIA));
	}
//	@AfterClass
	public void testDelete() {
		offerDao.deleteOffer(offerId);
		Assert.state(offerDao.countOffers()==0);
	}
}