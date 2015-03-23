package com.att.datalake.loco.sqlgenerator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.offercriteria.OfferDetailToSqlBridge;
import com.att.datalake.loco.offercriteria.OfferParser;
import com.att.datalake.loco.offercriteria.RuntimeSyntaxBuilder;
import com.att.datalake.loco.offercriteria.impl.CommonOffer1;
import com.att.datalake.loco.offercriteria.impl.Offer1;
import com.att.datalake.loco.offercriteria.impl.OfferBuilder;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.util.Utility;

@ContextConfiguration
public class CommonOffer1Test extends AbstractTestNGSpringContextTests {

	@Configuration
	static class config {
		@Bean
		@Qualifier("commonOffer1")
		OfferBuilder commonOffer1() {
			return new CommonOffer1();
		}
		@Bean
		@Qualifier("offer1")
		OfferBuilder offerBuilder() {
			return new Offer1();
		}
		@Bean
		OfferParser offerParser() {
			return new OfferParser();
		}
		@Bean
		RuntimeSyntaxBuilder runtimeSyntaxBuilder() {
			return new RuntimeSyntaxBuilder();
		}
		@Bean
		OfferDetailToSqlBridge offerDetailToSqlBridge() {
			return new OfferDetailToSqlBridge();
		}
		@Bean
		SQLClauseBuilder sqlClauseBuilder() {
			return new SQLClauseBuilder();
		}
		@Bean
		SQLStatementBuilder sqlStatementBuilder() {
			return new SQLStatementBuilder();
		}
	}
	@Autowired
	@Qualifier("commonOffer1")
	private OfferBuilder commonOffer1;
	@Autowired
	@Qualifier("offer1")
	private OfferBuilder offer1;
	@Autowired
	private OfferParser op;
	@Autowired
	private RuntimeSyntaxBuilder rb;
	
	@BeforeClass
	public void init() {
		op.setFilename("input/offer.csv");
	}
	@Test
	public void testOffer1() {
		List<OfferSpecification> offers = op.parse();
		Map<String, String> sqls = rb.build(offers);
		for (Entry<String, String> s: sqls.entrySet()) {
			System.out.println(s.getKey()+"=>  "+ Utility.prettyPrint(s.getValue()));
		}
	}
}