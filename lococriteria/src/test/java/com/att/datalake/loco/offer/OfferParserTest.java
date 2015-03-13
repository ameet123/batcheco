package com.att.datalake.loco.offer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.offercriteria.OfferParser;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.offercriteria.model.OfferSpecification.Detail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * test that the {@link OfferParser} is able to generate the model object from
 * file
 * 
 * @author ac2211
 *
 */
@ContextConfiguration
public class OfferParserTest extends AbstractTestNGSpringContextTests {

	@Configuration
	static class config {

		@Bean
		OfferParser offerParser() {
			return new OfferParser();
		}
		@Bean
		public static Gson gson() {
			return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		}
	}
	@Autowired
	private Gson gson;
	@Autowired
	private OfferParser op;

	@BeforeClass
	public void init() {
		op.setFilename("input/offer.csv");
	}

	@Test
	public void testOffer() {
		List<OfferSpecification> offers = op.parse();
		
		System.out.println("Offers size:" + offers.size());
		for (OfferSpecification o : offers) {
			System.out.println(gson.toJson(o));
			System.out.println(o.getOfferId());
			
			for (Detail d : o.getDetails()) {
				System.out.println("criterionType:" + d.getCriterionType() + " ID:" + d.getCriterionId()
						+ " Apply Object:" + String.join(",", d.getCriterionApplyObject()) + " Values:"
						+ String.join(",", d.getCriterionValues()));
			}
		}
	}
}