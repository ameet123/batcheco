package com.att.datalake.loco.preproc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.preproc.model.PreProcSpec;
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
	private List<PreProcSpec> preProcList;
	
	@BeforeClass
	public void init() {
		pr.setPreProcFile("input/preproc.csv");
	}
	/**
	 * ensure that we not only get all the paramters in correctly, but
	 * we also do not break any columns based on comma and parenthesis
	 * for e.g. we want MOD(BAN,1000) PARTITION_KEY to be extracted as a single token
	 */
	@Test
	public void testParse() {
		preProcList = pr.parse();		
		System.out.println("PreProc size:" + preProcList.size());
		Pattern p = Pattern.compile("(^[^\\(]+|(?<=\\()[^\\)]+(?=\\)))");
		Matcher m;
		
		for (PreProcSpec o : preProcList) {
			System.out.println(gson.toJson(o));
			System.out.println(o.getOfferId());
			
			for (PreProcSpec.ProcDetail d: o.getProcDetail()) {
				for (String c: d.getLeftColumns()) {
					System.out.println("Col:"+c);
					m = p.matcher(c);
					Assert.state(m.find());
				}
			}
		}
	}
}