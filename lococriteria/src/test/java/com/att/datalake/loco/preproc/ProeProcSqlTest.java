package com.att.datalake.loco.preproc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.att.datalake.loco.offercriteria.model.PreProcSpec;
import com.att.datalake.loco.preproc.builder.AllPreProcSqlBuilder;
import com.att.datalake.loco.preproc.builder.PredicateBuilder;
import com.att.datalake.loco.preproc.builder.SelectColMapBuilder;
import com.att.datalake.loco.preproc.builder.SqlFromComponentBuilder;
import com.att.datalake.loco.preproc.builder.TableClauseBuilder;
import com.att.datalake.loco.sqlgenerator.SQLClauseBuilder;
import com.att.datalake.loco.sqlgenerator.SQLStatementBuilder;
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
		PreProcessingParser preProcessingParser() {
			return new PreProcessingParser();
		}
		@Bean
		public SQLStatementBuilder sQLStatementBuilder() {
			return new SQLStatementBuilder();
		}
		@Bean
		public SelectColMapBuilder selectColMapBuilder() {
			return new SelectColMapBuilder();
		}
		@Bean
		public SqlFromComponentBuilder sqlFromComponentBuilder() {
			return new SqlFromComponentBuilder();
		}
		@Bean
		public PredicateBuilder predicateBuilder() {
			return new PredicateBuilder();
		}
		@Bean
		@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
		public TableClauseBuilder tableClauseBuilder() {
			return new TableClauseBuilder();
		}
		@Bean
		public AllPreProcSqlBuilder commonBuilder() {
			return new AllPreProcSqlBuilder();
		}
		@Bean
		public SQLClauseBuilder sqlClauseBuilder() {
			return new SQLClauseBuilder();
		}
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
		System.out.println("Size:"+preProcList.size());
		cb.build(preProcList);
	}
}
