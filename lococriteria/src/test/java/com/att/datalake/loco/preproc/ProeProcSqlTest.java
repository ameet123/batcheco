package com.att.datalake.loco.preproc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.preproc.builder.AllPreProcSqlBuilder;
import com.att.datalake.loco.preproc.builder.SelectColMapBuilder;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;

/**
 * generate sql from {@link PreProcSpec}
 * we do not use sqlgenerator package in component scan because there are some tests
 * in that package which are doing @Bean and instantiating some beans from those package
 * which overlap and we get 3 beans instead of one. Once we remove instantiation in those classes
 * by doing like here, i.e. componentscan, then we can remove these bean definitions
 * @author ac2211
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { ProeProcSqlTest.class })
@Configuration
@ComponentScan({ "com.att.datalake.loco.preproc.builder", "com.att.datalake.loco.preproc", "com.att.datalake.loco.offerconfiguration" })
public class ProeProcSqlTest extends AbstractTestNGSpringContextTests {
	
	@Bean
	public SQLStatementBuilder sQLStatementBuilder() {
		return new SQLStatementBuilder();
	}
	@Bean
	public SQLClauseBuilder sqlClauseBuilder() {
		return new SQLClauseBuilder();
	}

	@Autowired
	private SelectColMapBuilder selectColMapBuilder;
	@Autowired
	private PreProcessingParser pr;
	private List<PreProcSpec> preProcList;
	@Autowired
	private AllPreProcSqlBuilder cb;

	@BeforeClass
	public void init() {
		pr.setPreProcFile("input/preproc.csv");
		preProcList = pr.parse();
	}

	@Test
	public void testPreProc() {
		System.out.println("Size:" + preProcList.size());
		cb.build(preProcList);
	}
}
