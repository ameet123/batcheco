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
import com.att.datalake.loco.preproc.builder.CommonBuilder;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * generate sql from {@link PreProcSpec}
 * @author ac2211
 *
 */
@ContextConfiguration
public class ProeProcSqlTest extends AbstractTestNGSpringContextTests {

	@Configuration
	static class config {

		@Bean
		public static Gson gson() {
			return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		}
		@Bean
		public CommonBuilder commonBuilder() {
			return new CommonBuilder();
		}
		@Bean
		public SQLClauseBuilder sqlClauseBuilder() {
			return new SQLClauseBuilder();
		}
	}
	@Autowired
	private PreProcessingParser pr;
	private List<PreProcSpec> preProcList;
	@Autowired
	private CommonBuilder cb;
	
	@BeforeClass
	public void init() {
		pr.setPreProcFile("input/preproc.csv");
		preProcList = pr.parse();
	}
	
	@Test
	public void testPreProc() {
		for (PreProcSpec p: preProcList) {
			cb.build(p);
		}
	}
}
