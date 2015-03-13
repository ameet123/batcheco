package com.att.datalake.loco.sqlgenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.att.datalake.loco.util.Utility;

/**
 * test sql syntax generator
 * 
 * @author ac2211
 *
 */
@ContextConfiguration
public class MultiSQLBuilderTest extends AbstractTestNGSpringContextTests {
	@Autowired
	private SQLClauseBuilder sql;
	@Autowired
	private SQLStatementBuilder complete;
	
	private String column = "feature_group";
	private List<String> featureGroups;
	private List<String> featureCodes;
	private List<String> predicates;
	private Map<String, String> columns; // select clause
	private Map<String, String> transformMap;
	private Map<String, String> fromChargeMap;
	private Map<String, String> fromAdjustmentMap;

	// for complete sql
	private String select;
	private String from;
	private String where;
	private String groupby;
	
	private String chargeSelect;
	private String adjustmentSelect;
	
	@Configuration
	static class config {
		@Bean
		SQLClauseBuilder sQLClauseBuilder() {
			return new SQLClauseBuilder();
		}
		@Bean
		SQLStatementBuilder completeSQLBuilder() {
			return new SQLStatementBuilder();
		}
	}

	@BeforeClass
	public void init() {
		System.out.println(" I am in:"+this.getClass().getCanonicalName());
		
		fromChargeMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("loco_charge", null);
			}
		};
		fromAdjustmentMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("loco_adjustment", null);
			}
		};
		
		transformMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("ACTV_AMT", "Sum");
			}
		};
		columns = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("BAN", null);
				put("SUBSCRIBER_NO", null);
				put("FINANCE_EFF_DATE", null);
				put("PARTITION_KEY", null);
				put("ACTV_BILL_SEQ_NO", null);
				put("ACT_DATE", null);
				put("FEATURE_CODE", null);
				put("SOC", null);
				put("ACTV_AMT", null);
			}
		};
		predicates = new ArrayList<String>();
		featureGroups = new ArrayList<String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				add("TF");
				add("VF");
				add("PF");
			}
		};
		featureCodes = new ArrayList<String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				add("AMBMIN");
				add("AMBREV");
				add("CVAB");
				add("CVAT");
				add("CVBK");
				add("CVCL");
				add("CVDP");
				add("CVIC");
				add("CVMT");
				add("CVOC");
				add("CVRM");
				add("CVTL");
				add("DCK");
				add("DEPOST");
				add("GENDEP");
				add("LTPYM");
				add("LTPYMM");
				add("REFUND");
				add("RFNDEP");
				add("RSLTFE");
				add("TLRNCE");
				add("XMLPA");
				add("PAFM");
			}
		};
	}


	@Test(groups = {"11"})
	public void testExclusionGen() {
		String s = sql.inOutPredicate(column, featureGroups, String.class, true);
		predicates.add(s);
		System.out.println(s);
	}

	@Test(groups = {"11"})
	public void testFeatureCodeEx() {
		String s = sql.inOutPredicate("feature_code", featureCodes, String.class, true);
		predicates.add(s);
		System.out.println(s);
	}
	
	@Test(groups = {"21"} , dependsOnGroups = {"11"})
	public void tesFrom() {
		String s = sql.from(fromChargeMap, null);
		this.from = s;
		System.out.println(s);
	}

	

	@Test(groups = {"31"},dependsOnGroups = {"11", "21"})
	public void testWhere() {
		String s = sql.where(predicates, true);
		System.out.println(s);
		this.where = s;
	}
	
	@Test(groups = {"31"}, dependsOnGroups = {"11", "21"}, dependsOnMethods = {"testWhere"})
	public void testSELECT() {
		String s = sql.select(columns, null);
		System.out.println(s);
		this.select = s;
	}
	
	@Test(groups = {"3"},dependsOnGroups = {"1", "2"})
	public void testGroupBy() {
		String s = sql.groupBy(columns);
		System.out.println(s);
//		this.groupby = s;
	}
	
	
	@Test(groups = {"41"}, dependsOnGroups = {"31"})
	public void testStatement() {
		complete.doFrom(from);
		complete.doSelect(select);
		complete.doGroupBy(groupby);
		complete.doWhere(where);
		this.chargeSelect = complete.build();
		System.out.println(Utility.prettyPrint(chargeSelect));
	}
	
	// CONSTRUCT similar looking ADJUSTMENT
	@Test(groups = {"51"}, dependsOnGroups = {"41"})
	public void testBuildAdjustment() {
		complete.reset();
		predicates.clear();
		predicates.add(sql.inOutPredicate(column, featureGroups, String.class, true));
		predicates.add(sql.inOutPredicate("feature_code", featureCodes, String.class, true));
		complete.doWhere(sql.where(predicates, true));
		complete.doFrom(sql.from(fromAdjustmentMap, null));
		complete.doSelect(sql.select(columns, null));
		this.adjustmentSelect = complete.build();
		System.out.println(Utility.prettyPrint(adjustmentSelect));
	}
	@Test(groups = {"61"}, dependsOnGroups = {"51"})
	public void testMultiSQL() {
		complete.reset();
		complete.doSelect(sql.select(columns, transformMap));
		List<String> stmts = new ArrayList<String>();
		stmts.add(chargeSelect);
		stmts.add(adjustmentSelect);
		String unionTable = sql.unionAll(stmts);
		Map<String, String> from2TableMap = new HashMap<String, String>();
		from2TableMap.put(unionTable, "a");
		Map<String, Boolean> inlineMap = new HashMap<String, Boolean>();
		inlineMap.put(unionTable, true);
		String from = sql.from(from2TableMap, inlineMap);
		complete.doFrom(from);
		complete.doGroupBy(sql.groupBy(columns));
		
		
		System.out.println(Utility.prettyPrint(complete.build()));		 
	}
	
	@BeforeMethod
	private void startingTest(Method method) {
		System.out.println("\n******* " + method.getName() + " ********"+"\n");
	}
}