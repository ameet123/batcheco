package com.att.datalake.loco.preproc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ContextConfiguration
public class PreProcParserTest extends AbstractTestNGSpringContextTests {

	@Configuration
	static class config {

		@Bean
		PreProcessingParser preProcessingParser() {
			return new PreProcessingParser();
		}
		@Bean
		public static Gson gson() {
			return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		}
	}
	@Autowired
	private Gson gson;
	@Autowired
	private PreProcessingParser pr;

	@BeforeClass
	public void init() {
		pr.setPreProcFile("input/preproc.csv");
	}
	@Test
	public void testOffer() {
		List<PreProcSpec> preProcList = pr.parse();
		
		System.out.println("PreProc size:" + preProcList.size());
		for (PreProcSpec o : preProcList) {
			System.out.println(gson.toJson(o));
			System.out.println(o.getOfferId());
			
//			for (Detail d : o.getDetails()) {
//				System.out.println("criterionType:" + d.getCriterionType() + " ID:" + d.getCriterionId()
//						+ " Apply Object:" + String.join(",", d.getCriterionApplyObject()) + " Values:"
//						+ String.join(",", d.getCriterionValues()));
//			}
		}
	}
}
